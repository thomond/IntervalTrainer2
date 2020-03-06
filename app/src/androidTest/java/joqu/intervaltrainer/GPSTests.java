package joqu.intervaltrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.services.LiveSessionService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class GPSTests {
    private boolean passed;
    AppDao mAppDao;
    AppDatabase mDB;
    Context appContext;
    Intent intent;
    BroadcastReceiver mReceiver;

    @Before
    public void createDb(){
        // Context of the app under test.
         appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("joqu.intervaltrainer", appContext.getPackageName());
        // Get an instance of the App Database and send the population callback
        mDB = AppDatabase.getInMemDB(appContext, null);
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        mAppDao = mDB.appDao();

        intent = new Intent(appContext, LiveSessionService.class);



        // Fill the intent with the temp id and start the service
        intent.putExtra(Const.INTENT_EXTRA_TEMPLATE_NAME_STRING, "Debug Test Session");
        intent.putExtra(Const.INTENT_EXTRA_DO_TIMER_BOOL, false);


    }


    @Test
    public void GPSServiceTest() throws InterruptedException {

        // Start service
        if (Build.VERSION.SDK_INT >= 26) {
            // Start in foreground; id: non-zro identifier, Notification object to show in taskbar
            appContext.startForegroundService(intent);
        } else
            appContext.startService(intent);


        // set up BroadcastReciever  to updates from service
        mReceiver = new BroadcastReceiver() {
            int timersDone = 0;

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
                    Log.d(context.getString(R.string.app_name), "GPS Data: " + GPS_lat + ", " + GPS_long + ".");
                } else if (action == Const.BROADCAST_SVC_STOPPED) {
                    passed = true;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_GPS_UPDATE);
        intentFilter.addAction(Const.BROADCAST_SVC_STOPPED);
        LocalBroadcastManager.getInstance(appContext).registerReceiver(mReceiver, intentFilter);


        try {
            while (!passed)
                Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Unregister whn no longer needed
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(mReceiver);

    }

}