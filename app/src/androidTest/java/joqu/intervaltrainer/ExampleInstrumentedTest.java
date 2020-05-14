package joqu.intervaltrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.SavedSession;
import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.entities.Session;
import joqu.intervaltrainer.model.entities.Template;
import joqu.intervaltrainer.services.LiveSessionService;
import joqu.intervaltrainer.services.ServiceTTSManager;

import static android.content.Context.VIBRATOR_SERVICE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private boolean passed;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("joqu.intervaltrainer", appContext.getPackageName());
    }

    @Test
    public void insertTest() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.getInMemDB(appContext);
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        AppDao mAppDao = mDB.appDao();




        Thread.sleep(1000);
        List<Session> mSessions = mAppDao.getAllSessions();
        List<IntervalData> mData = mAppDao.getAllIntervalData();
        List<Template> mTemplate = mAppDao.getAllTemplates();
        List<Interval> mIntervalTypes = mAppDao.getAllIntervals();
    }

    @Test
    public void ServiceTest() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(appContext, LiveSessionService.class);
        BroadcastReceiver mReceiver;
        // Get an instance of the App Database
        final AppDatabase mDB = AppDatabase.getInMemDB(appContext);
       // mDB.populateDB();


        // Fill the intent with the trst name and enable mock locations and start the service
        intent.putExtra(Const.INTENT_EXTRA_TEMPLATE_NAME_STRING,"Debug Test Session");

        // Start service
        if (Build.VERSION.SDK_INT >= 26) {
            // Start in foreground; id: non-zro identifier, Notification object to show in taskbar
            appContext.startForegroundService(intent);
        }else
            appContext.startService(intent);


        // set up BroadcastReciever  to updates from service
        mReceiver = new BroadcastReceiver() {
            int timersDone=0;
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == Const.BROADCAST_GPS_UPDATE) {
                    float GPS_dist, GPS_speed;
                    double GPS_lat, GPS_long;
                    GPS_dist = intent.getFloatExtra(Const.INTENT_EXTRA_GPS_DIST_FLOAT, Float.NaN);
                    GPS_speed = intent.getFloatExtra(Const.INTENT_EXTRA_GPS_SPEED_FLOAT, Float.NaN);
                    GPS_lat = intent.getDoubleExtra(Const.INTENT_EXTRA_GPS_LAT_DOUBLE, Double.NaN);
                    GPS_long = intent.getDoubleExtra(Const.INTENT_EXTRA_GPS_LONG_DOUBLE, Double.NaN);
                    assertNotEquals(GPS_dist, Float.NaN);
                    assertNotEquals(GPS_speed, Float.NaN);
                    assertNotEquals(GPS_lat, Double.NaN);
                    assertNotEquals(GPS_long, Double.NaN);
                    // Log.i(context.getString(R.string.app_name),String.valueOf(value/1000)+" seconds.");
                }if (action == Const.BROADCAST_COUNTDOWN_UPDATE) {
                    long value;
                    value = intent.getLongExtra("millisUntilFinished", 0);
                    Log.i(context.getString(R.string.app_name),String.valueOf(value/1000)+" seconds.");
                } else if (action == Const.BROADCAST_SVC_STOPPED){
                    passed=true;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_GPS_UPDATE);
        intentFilter.addAction(Const.BROADCAST_COUNTDOWN_UPDATE);
        intentFilter.addAction(Const.BROADCAST_COUNTDOWN_DONE);
        intentFilter.addAction(Const.BROADCAST_SVC_STOPPED);
        LocalBroadcastManager.getInstance(appContext).registerReceiver(mReceiver,intentFilter);

        try {
            while (!passed)
                Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //assertTrue(false);// Fail if timer ends

        // Unregister whn no longer needed
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(mReceiver);

    }


    @Test
    public void CountdownServiceTest(){
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(appContext, LiveSessionService.class);
        BroadcastReceiver mReceiver;
        // Get an instance of the App Database
        final AppDatabase mDB = AppDatabase.getInMemDB(appContext);
        //mDB.populateDB();

        // Fill the intent with the temp id and start the service
        intent.putExtra(Const.INTENT_EXTRA_TEMPLATE_NAME_STRING,"Debug Test Session");
        // Start service
        if (Build.VERSION.SDK_INT >= 26) {
            // Start in foreground; id: non-zro identifier, Notification object to show in taskbar
            appContext.startForegroundService(intent);
        }else
            appContext.startService(intent);


        // set up BroadcastReciever  to updates from service
        mReceiver = new BroadcastReceiver() {
            int timersDone=0;
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == Const.BROADCAST_COUNTDOWN_UPDATE) {
                    long value;
                    value = intent.getLongExtra("millisUntilFinished", 0);
                    Log.i(context.getString(R.string.app_name),String.valueOf(value/1000)+" seconds.");
                }else if (action == Const.BROADCAST_SVC_STOPPED){
                    passed=true;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_COUNTDOWN_UPDATE);
        intentFilter.addAction(Const.BROADCAST_COUNTDOWN_DONE);
        intentFilter.addAction(Const.BROADCAST_SVC_STOPPED);
        LocalBroadcastManager.getInstance(appContext).registerReceiver(mReceiver,intentFilter);

        try {
            while (!passed)
                Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<SavedSession> sessions = SavedSession.getAllSessions(mDB.appDao());

        Log.d(Const.APP_NAME, "GPSServiceTest: " + sessions.get(0).getString());

    }
    @Test
    public void TTSTest(){
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ServiceTTSManager.init(appContext);

        while(!ServiceTTSManager.isInitialized()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ServiceTTSManager.speak("Hello, World","test");
        while(ServiceTTSManager.isSpeaking()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Test
    public void vibrateTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) appContext.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) appContext.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }
}
