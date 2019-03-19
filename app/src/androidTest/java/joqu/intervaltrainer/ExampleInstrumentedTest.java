package joqu.intervaltrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;

import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.Interval;
import joqu.intervaltrainer.model.IntervalData;
import joqu.intervaltrainer.model.Session;
import joqu.intervaltrainer.model.Template;

import static org.junit.Assert.*;

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
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("joqu.intervaltrainer", appContext.getPackageName());
    }

    @Test
    public void insertTest() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.getDB(appContext);
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        AppDao mAppDao = mDB.appDao();
        new AppDatabase.InsertAsyncTask(mAppDao).execute(new Session(0,"20180101","20190102","this is a test"));

        for (int i=0;i<5;i++)
        {
            new AppDatabase.InsertAsyncTask(mAppDao).execute(new IntervalData(0,1,"test "+i));
        }


        new AppDatabase.InsertAsyncTask(mAppDao).execute(new Template(0,"Template","Template 1","Example Template"));

        for (int i=0;i<5;i++)
        {
            new AppDatabase.InsertAsyncTask(mAppDao).execute(new Interval(0,"20;minutes",0,i));
        }

        Thread.sleep(1000);
        List<Session> mSessions = mAppDao.getAllSessions();
        List<IntervalData> mData = mAppDao.getAllIntervalData();
        List<Template> mTemplate = mAppDao.getAllTemplates();
        List<Interval> mIntervalTypes = mAppDao.getAllIntervals();
    }

    @Test
    public void daoTest() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        
        assertEquals("joqu.intervaltrainer", appContext.getPackageName());

        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.getDB(appContext);
        new AppDatabase.PopulateAppDbAsyncTask(mDB).execute();
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        AppDao mAppDao = mDB.appDao();

        // Wait for possibly a little time for the test to be written
        //Thread.sleep(6000);

        List<Session> mSessions = mAppDao.getAllSessions();
        List<IntervalData> mData = mAppDao.getAllIntervalData();
        List<Template> mTemplate = mAppDao.getAllTemplates();
        List<Interval> mIntervalTypes = mAppDao.getAllIntervals();
    }


    @Test
    public void CountdownServiceTest(){
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(appContext, LiveSessionService.class);
        BroadcastReceiver mReceiver;
        // Get an instance of the App Database
        final AppDatabase mDB = AppDatabase.getDB(appContext);

        new AsyncTask() {
            @Override
            protected Void doInBackground(Object... objects) {
                mDB.clearAllTables();

                Template mTem = new Template("Test Template", "test", "Test Template");
                Log.i(Const.TAG, mTem.hashCode() + " added");
                mDB.appDao().addTemplate(mTem);
                for (int i = 0; i < 5; i++) {
                    long rand = new Random().nextInt(10) * 1000;
                    Interval interval = new Interval(0, "" + rand, 1, i);
                    mDB.appDao().addInterval(interval);
                    Log.i(Const.TAG, "Interval" + i + " added: " + interval.toString());
                }

                return null;
            }
        }.execute();

        // Fill the intent with the temp id and start the service
        intent.putExtra(Const.INTENT_EXTRA_TEMPLATE_ID_INT,1);
        intent.putExtra(Const.INTENT_EXTRA_DO_GPS_BOOL, false);
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

        AppDao mAppDao = AppDatabase.getDB(appContext).appDao();
        List<Session> mSessions =  mAppDao.getAllSessions();
        List<IntervalData> mData = mAppDao.getAllIntervalData();

        //assertTrue(false);// Fail if timer ends

        // Unregister whn no longer needed
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(mReceiver);

    }

    @Test
    public void ServiceTest(){
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(appContext, LiveSessionService.class);

        BroadcastReceiver mReceiver;
        // Get an instance of the App Database
        final AppDatabase mDB = AppDatabase.getDB(appContext);

        new AsyncTask() {
            @Override
            protected Void doInBackground(Object... objects) {
                mDB.clearAllTables();

                Template mTem = new Template("Test Template", "test", "Test Template");
                Log.i(Const.TAG, mTem.hashCode() + " added");
                mDB.appDao().addTemplate(mTem);
                for (int i = 0; i < 5; i++) {
                    long rand = new Random().nextInt(60) * 1000;
                    Interval interval = new Interval(0, "" + rand, 1, i);
                    mDB.appDao().addInterval(interval);
                    Log.i(Const.TAG, "Interval" + i + " added: " + interval.toString());
                }

                return null;
            }
        }.execute();



    }
}
