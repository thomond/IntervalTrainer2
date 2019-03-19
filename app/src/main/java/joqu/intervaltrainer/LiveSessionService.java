package joqu.intervaltrainer;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.Interval;
import joqu.intervaltrainer.model.IntervalData;
import joqu.intervaltrainer.model.Session;
import joqu.intervaltrainer.model.Template;

import static joqu.intervaltrainer.Const.*;


public final class LiveSessionService extends Service {
    private Looper serviceLooper;
    private LiveSessionHandler serviceHandler;
    private AppDatabase DB;
    // Template objs for session to be based on
    Template mTemplate;
    Collection<Interval> mTemplateIntervals;
    // Thread workers
    CountdownRunnable mCountdownRunnable;
    GeoTrackerRunnable mGeoTrackerRunnable;
    // session related objs
    Session mSession;
    LinkedList<IntervalData> mIntervalData;
    IntervalData mIntervalDatum;

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



    private AppDao mDao;


    private class CountdownRunnable implements Runnable {
        LinkedList<IntervalTimer> mCoundownTimers = new LinkedList<>();
        IntervalTimer mRunningTimer;
        private long lastTick;// last recorded timer val for resuming
        boolean isPaused = false;
        private int mType;
        private int mIntervalIndex = 0;

        private class IntervalTimer extends CountDownTimer {

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

            public void onTick(long millisUntilFinished) {
                // TODO: perform tick actions
                // Broadcast millisUntilFinished on tick
                Intent intent = new Intent();
                intent.setAction(getString(R.string.BROADCAST_COUNTDOWN_UPDATE));
                intent.putExtra(Const.INTENT_EXTRA_TIMELEFT_LONG, millisUntilFinished);
                intent.putExtra(Const.INTENT_EXTRA_COUNTDOWN_TYPE_INT, mType);
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
                // Add final data to interval and replace in list
                if(mIntervalDatum!=null) {
                    mIntervalDatum.ended = String.valueOf(new Date().getTime());
                    mIntervalData.add(mIntervalIndex, mIntervalDatum);
                }
                // Pop the list and start next timer
                if( !mCoundownTimers.isEmpty()){
                    mRunningTimer = mCoundownTimers.pop();
                    mRunningTimer.start();
                    // Go to next intervaldata record in list
                    mIntervalDatum = mIntervalData.get(mIntervalIndex++);
                    mIntervalDatum.started = String.valueOf(new Date().getTime());
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
                while (mTemplateIntervals == null)
                    Thread.sleep(200);

                for (Interval mInterval : mTemplateIntervals) {
                    // FIXME: decode parameters
                    long millisFinished = Long.valueOf(mInterval.parameters);
                    mType = mInterval.type;
                    // push a new timer to the list
                    mCoundownTimers.push(new IntervalTimer(millisFinished, 500));
                    mIntervalData.push(new IntervalData(mInterval.id,mSession.id));
                }
                // Start the first countdown and grab the first intervaldata record obj
                mRunningTimer = mCoundownTimers.pop();
                mRunningTimer.start();
                mIntervalDatum = mIntervalData.get(mIntervalIndex++);
                mIntervalDatum.started = String.valueOf(new Date().getTime());


            }catch (InterruptedException e) { e.printStackTrace();}
            catch (NullPointerException e) { Log.e(TAG,"NULL value Encountered: ");e.printStackTrace();}
        }
    }

    private class GeoTrackerRunnable implements Runnable {
        Location mLastLocation;
        float mDistance;
        LocationCallback locationCallback = new LocationCallback() { // Callback for location update
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    // broadcast  location data
                    Intent intent = new Intent();
                    intent.setAction(Const.BROADCAST_GPS_UPDATE);
                    intent.putExtra(Const.INTENT_EXTRA_GPS_LONG_DOUBLE, location.getLongitude());
                    intent.putExtra(Const.INTENT_EXTRA_GPS_LAT_DOUBLE, location.getLatitude());
                    mSession.addLocation(location);
                    // Calculate distance travelled
                    // FIXME: deal with accuracy issues
                    if (mLastLocation!= null)
                        mDistance += location.distanceTo(mLastLocation);
                    intent.putExtra(Const.INTENT_EXTRA_GPS_DIST_FLOAT,mDistance);
                    intent.putExtra(Const.INTENT_EXTRA_GPS_SPEED_FLOAT, location.getSpeed());
                    mSession.distance = mDistance;

                    LocalBroadcastManager
                            .getInstance(getApplicationContext()).sendBroadcast(intent);
                    Log.d(TAG,"Accuracy: "+location.getAccuracy());
                    Log.d(TAG,"Location Update:"+location.toString());
                    mLastLocation = location;
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
                mLocationRequest.setInterval(5000);
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
                        mSession.ended = String.valueOf(new Date().getTime());
                        // Do insert
                        new AppDatabase.InsertAsyncTask(mDao).execute(mSession, mIntervalData);
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
            // Mostly used for debugging
            final boolean lDoTimer = intent.getBooleanExtra(Const.INTENT_EXTRA_DO_TIMER_BOOL, true);
            final boolean lDoGPS = intent.getBooleanExtra(Const.INTENT_EXTRA_DO_GPS_BOOL, true);

            // FIXME:  tthis may leak, make static
                new AsyncTask(){
                    @Override
                    protected Void doInBackground(Object[] objects) {
                        mTemplate = mDao.getTemplateById(recvedTemplateId);
                        mTemplateIntervals = mDao.getIntervalsTemplateById(recvedTemplateId);
                        if (mTemplate!=null) Log.d(TAG,"Read template from DB: "+mTemplate.name);
                        Log.d(TAG,"Read " +mTemplateIntervals.size() + " Interval records");

                        return null;
                    }
                }.execute();

            // Wait for data read
            while(mTemplate==null) Thread.sleep(200);
            // init member objects for saved data

            mSession = new Session(mTemplate.id,String.valueOf(new Date().getTime()));
            mSession.title = "Session on: " + new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault()).format(new Date());
            mIntervalData = new LinkedList<>();

            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job
            //Message msg = serviceHandler.obtainMessage();
            //msg.arg1 = startId;

            //serviceHandler.sendMessage(msg);
            // Process starting intent and Populate service template
    //        String templateJSON = intent.getStringExtra("Template");
    //        if(templateJSON!=null){
    //            mTemplate = Template.fromJSON(templateJSON);
    //            if(mTemplate==null){
    //                Log.e("","Intent parameters invalid: templateJSON");
    //                throw new IllegalArgumentException();
    //            }
    //
    //        }
    //        String[] templateIntervalsJSON = intent.getStringArrayExtra("Intervals");
    //        if(templateIntervalsJSON!=null){
    //            for (String i : templateIntervalsJSON)
    //            {
    //                mTemplateIntervals.add(Interval.fromJSON(i));
    //            }
    //    }




            // Start up the thread running the service. Note that we create a
            // separate thread because the service normally runs in the process's
            // main thread, which we don't want to block. We also make it
            // background priority so CPU-intensive work doesn't disrupt our UI.
            HandlerThread thread = new HandlerThread("ServiceStartArguments",Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = thread.getLooper();
            serviceHandler = new LiveSessionHandler(serviceLooper);



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


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    private void stop() {

        if (mCountdownRunnable.mRunningTimer!= null) mCountdownRunnable.mRunningTimer.cancel();
        if (mGeoTrackerRunnable != null)
            if (mGeoTrackerRunnable.mRequestingLocationUpdates)
                mGeoTrackerRunnable.stopTracking();
        Intent send = new Intent();
        send.setAction(Const.BROADCAST_SVC_STOPPED);
        LocalBroadcastManager
                .getInstance(getApplicationContext()).sendBroadcast(send);
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

        // For later SDKs use a foreground service
        if (Build.VERSION.SDK_INT >= 26){
            // Register notification channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.app_name), "IntervalTrainer Channel", importance);
            channel.setDescription("IntervalTrainer Channel");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            // Create a Notification
            Notification mNotification = new Notification.Builder(this,getString(R.string.app_name)).setContentTitle("Session active").setContentText("...").build();
            startForeground(1, mNotification);
        }




    }

    @Override
    public void onDestroy() {
        Log.i(getBaseContext().getString(R.string.app_name),"Service finished...");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
