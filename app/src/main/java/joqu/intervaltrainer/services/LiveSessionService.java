package joqu.intervaltrainer.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedList;

import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.R;
import joqu.intervaltrainer.Util;
import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.SavedSession;
import joqu.intervaltrainer.model.SessionTemplate;

import static joqu.intervaltrainer.Const.*;


public final class LiveSessionService extends Service {
    private Looper serviceLooper;
    private LiveSessionHandler serviceHandler;
    private AppDatabase DB;
    // Template objs for session to be based on
    SessionTemplate mSessionTemplate;
    // Thread workers
    CountdownRunnable mCountdownRunnable;
    GeoTrackerRunnable mGeoTrackerRunnable;
    // session related objs
    SavedSession mSavedSession;


    // Class to deal with session state such as Pause/Resume that are recieved via broadcasts
    BroadcastReceiver mServiceStateReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == Const.BROADCAST_SVC_STOP) {
                stop();
            } else if (action == Const.BROADCAST_SVC_RESUME) {
                // TODO: implememnt resuming of timer; Will have to save last recorded second of timer and create new timer
            }


            }
    };

    private ServiceTTSManager tts;

    private AppDao mDao;


    private class CountdownRunnable implements Runnable {
        LinkedList<IntervalTimer> mIntervalTimers = new LinkedList<>();
        IntervalTimer mIntervalTimer;
        private long lastTick;// last recorded timer val for resuming
        boolean isPaused = false;
        //private int intervalTotal = 0;// Stores total number of elements that was in in list
        //private int intervalIndex = 0;// Stores current position of interval in list

        private class IntervalTimer extends CountDownTimer {
            private int type;

            /**
             * @param millisInFuture    The number of millis in the future from the call
             *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
             *                          is called.
             * @param countDownInterval The interval along the way to receive
             *                          {@link #onTick(long)} callbacks.
             */
            public IntervalTimer(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);
            }


            public CountDownTimer startTimer(){

                int index = mIntervalTimers.indexOf(this);
                type = mSessionTemplate.getInterval(index).type;
                mSavedSession.addIntervalData(mSessionTemplate.getInterval(index));
                return start();
            }

            public void onTick(long millisUntilFinished) {
                // TODO: perform tick actions
                // Broadcast millisUntilFinished on tick
                Intent intent = new Intent();
                intent.setAction(getString(R.string.BROADCAST_COUNTDOWN_UPDATE));
                intent.putExtra(Const.INTENT_EXTRA_TIMELEFT_LONG, millisUntilFinished);
                intent.putExtra(Const.INTENT_EXTRA_COUNTDOWN_TYPE_INT, type);
                intent.putExtra(Const.INTENT_EXTRA_COUNTDOWN_TOTAL_INT,  mIntervalTimers.size());
                intent.putExtra(Const.INTENT_EXTRA_COUNTDOWN_INDEX_INT, mIntervalTimers.indexOf(this)+1);
                LocalBroadcastManager
                        .getInstance(getApplicationContext()).sendBroadcast(intent);
                Log.d(TAG,"Data broadcast from "+ this.hashCode()  + ": "+millisUntilFinished);
                lastTick = millisUntilFinished;
            }

            public void onFinish() {
                Log.d(TAG,"Timer " + this.hashCode() + " Ended.");
                Intent intent = new Intent();
                intent.setAction(Const.BROADCAST_COUNTDOWN_DONE);
                LocalBroadcastManager
                        .getInstance(getApplicationContext()).sendBroadcast(intent);

                int index = mIntervalTimers.indexOf(this);
                // Finalise the interval data
                mSavedSession.finaliseInterval();
                // Pop the list and start next timer
                if( !mIntervalTimers.isEmpty() && index < mIntervalTimers.size()-1){
                    mIntervalTimer = mIntervalTimers.get(index+1);
                    mIntervalTimer.startTimer();
                }else{
                    // Session is over, inform handler
                    Log.i(TAG, "Session finished");
                    serviceHandler.handleMessage(Message.obtain(serviceHandler,Const.MESSAGE_SESSION_DONE));
                }
            }

            
        }

        @Override
        public void run() {
            try {
                // Wait for data to be retrieved from DB
                while (!mSessionTemplate.hasTemplate())
                    Thread.sleep(200);


                for (Interval mInterval : mSessionTemplate.getIntervals()) {
                    // FIXME: decode time
                    long millisFinished = mInterval.time;
                    // push a new timer to the list
                    mIntervalTimers.push(new IntervalTimer(millisFinished, Const.TIMER_COUNTDOWN_INTERVAL_LONG));
                }
                // Start the first countdown and grab the first intervaldata record obj
                mIntervalTimer = mIntervalTimers.getFirst();
                mIntervalTimer.startTimer();

            }catch (InterruptedException e) { e.printStackTrace();}
            catch (NullPointerException e) { Log.e(TAG,"NULL value Encountered: ");e.printStackTrace();}
        }
    }

    private class GeoTrackerRunnable implements Runnable {


        LocationCallback locationCallback = new LocationCallback() { // Callback for location update
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    // Add location to saved session
                    mSavedSession.addNewLocation(location);

                    // broadcast  location data
                    Intent intent = new Intent();
                    intent.setAction(Const.BROADCAST_GPS_UPDATE);
                    intent.putExtra(Const.INTENT_EXTRA_GPS_LONG_DOUBLE, location.getLongitude());
                    intent.putExtra(Const.INTENT_EXTRA_GPS_LAT_DOUBLE, location.getLatitude());
                    intent.putExtra(Const.INTENT_EXTRA_GPS_DIST_FLOAT,mSavedSession.getCurrentInterval().distance);
                    intent.putExtra(Const.INTENT_EXTRA_GPS_SPEED_FLOAT, location.getSpeed());


                    LocalBroadcastManager
                            .getInstance(getApplicationContext()).sendBroadcast(intent);

                    Log.d(TAG,"Accuracy: "+location.getAccuracy());
                    Log.d(TAG,"Location Update:"+location.toString());
                }
            }
        };
        FusedLocationProviderClient mFusedLocationClient;
        LocationRequest mLocationRequest;
        Boolean mRequestingLocationUpdates;

        public void startTracking(){
            try
            {
                // Create new Location Client
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                // Formulate request based on define values
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setInterval(Const.LOCATION_REQ_INTERVAL_INT);
                mLocationRequest.setFastestInterval(1000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                Log.d(TAG,"Location Request Built: "+ mLocationRequest.toString());
                // bind callback and request to client and start tracking
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null );
                mRequestingLocationUpdates = true;
                Log.i(TAG,"Location Updates Requested");

            }catch(SecurityException e){
                Log.e(TAG,"Permissions Error for Location");

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        public void stopTracking(){
            if (mFusedLocationClient != null)
            {
                Log.i(Const.TAG,"Stopping Location Requests ");
                mFusedLocationClient.removeLocationUpdates(locationCallback);
                mRequestingLocationUpdates = false;
            }
        }


        @Override
        public void run() {
            startTracking();
        }

        private void resumeTracking() {
            // Verify permissions
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.e(TAG,"Permissions Failure ");
                return;
            }
            // bind callback and request to client and start tracking
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null );
            Log.i(TAG,"Location Updates Requested");
        }

    }

    private final class LiveSessionHandler extends Handler {
        public LiveSessionHandler(Looper looper) {

            super(looper);

        }

        @Override
        public void handleMessage(Message msg) {
            // Session related actions
            try {
                switch (msg.what){
                    case Const.MESSAGE_SESSION_DONE: {

                        // Check data nad then persist to DB
                        mSavedSession.finalise();
                        // Do insert
                        mSavedSession.saveAll(mDao);
                        stop();
                        break;
                    }
                    default:   Log.e(TAG, "Message not Recognized: "+msg.what); break;
                }
            } catch (Exception e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            //
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // Get DB instance
            DB = AppDatabase.getDB(getApplicationContext());
            mDao = DB.appDao();
            final int recvedTemplateId =  intent.getIntExtra("template_id",1);
            final String recvedTemplateName = intent.getStringExtra(Const.INTENT_EXTRA_TEMPLATE_NAME_STRING);
            // Mostly used for debugging
            final boolean lDoTimer = intent.getBooleanExtra(Const.INTENT_EXTRA_DO_TIMER_BOOL, true);
            final boolean lDoGPS = intent.getBooleanExtra(Const.INTENT_EXTRA_DO_GPS_BOOL, true);

            mSessionTemplate = new SessionTemplate();

            // Retriev based on session name or id
            if(  recvedTemplateName != null)
                mSessionTemplate.getFromDB(mDao, recvedTemplateName);
            else mSessionTemplate.getFromDB(mDao, recvedTemplateId);

            // Wait for data read
            while(!mSessionTemplate.hasTemplate()) Thread.sleep(200);

            // init member objects for saved data
            mSavedSession = new SavedSession();
            mSavedSession.init("Session on: " + Util.getDateStr("EEE, d MMM yyyy HH:mm"), mSessionTemplate.getId());

            HandlerThread thread = new HandlerThread("ServiceStartArguments",Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = thread.getLooper();
            serviceHandler = new LiveSessionHandler(serviceLooper);

           ServiceTTSManager.init(getApplicationContext());

            // Decalre and post Runnables to handler
            if(lDoTimer) {
                mCountdownRunnable = new CountdownRunnable();
                serviceHandler.post(mCountdownRunnable);
            }
            if(lDoGPS){
                mGeoTrackerRunnable = new GeoTrackerRunnable();
                serviceHandler.post(mGeoTrackerRunnable);
            }
            LocalBroadcastManager
                    .getInstance(getApplicationContext()).sendBroadcast(new Intent().setAction(Const.BROADCAST_SVC_STARTED));


        } catch (Exception e) {
            Log.e(TAG, "Service could not start...");
            e.printStackTrace();
        }
        return START_STICKY;
    }

    private void stop() {

        if (mCountdownRunnable.mIntervalTimer != null) mCountdownRunnable.mIntervalTimer.cancel();
        if (mGeoTrackerRunnable != null)
            if (mGeoTrackerRunnable.mRequestingLocationUpdates)
                mGeoTrackerRunnable.stopTracking();
        Intent send = new Intent();
        send.setAction(Const.BROADCAST_SVC_STOPPED);
        LocalBroadcastManager
                .getInstance(getApplicationContext()).sendBroadcast(send);
        ServiceTTSManager.stop();
        // Request system to stop notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0 );
        stopSelf();
    }


    private void resume(){

    }

    @Override
    public void onCreate() {
        Log.i(getBaseContext().getString(R.string.app_name),"Starting Service ...");

        // Register the service state broadcast receivers
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_SVC_STOP);
        intentFilter.addAction(Const.BROADCAST_SVC_PAUSE);
        intentFilter.addAction(Const.BROADCAST_SVC_RESUME);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mServiceStateReciever,intentFilter);


        // TODO: Friday 6 March 2020 22:29:55 GMT Add intents to notification to allow fir interaction

        Notification notification;

        // For later SDKs use a foreground service
        if (Build.VERSION.SDK_INT >= 26){
            // Register new notification channel
            NotificationChannel channel = new NotificationChannel(getString(R.string.app_name), "IntervalTrainer Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("IntervalTrainer Channel");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            // Build a notification for a running session
            notification = new Notification.Builder(this,channel.getId())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Session active")
                    .setContentText("Session Active").build();

            startForeground(1, notification);
        }else{
            // Build a notification for a running session
             notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Session active")
                    .setContentText("Session Active").build();

            // Request system to show notification
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }


    }

    @Override
    public void onDestroy() {
        Log.i(getBaseContext().getString(R.string.app_name),"Service finished...");
        Toast.makeText(this,"Tracking Has Finished", Toast.LENGTH_LONG).show();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
