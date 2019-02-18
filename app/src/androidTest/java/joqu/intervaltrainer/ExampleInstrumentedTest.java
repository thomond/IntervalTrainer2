package joqu.intervaltrainer;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import javax.xml.transform.Templates;

import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.Interval;
import joqu.intervaltrainer.model.IntervalData;
import joqu.intervaltrainer.model.Session;
import joqu.intervaltrainer.model.Template;
import joqu.intervaltrainer.ui.SavedSessionViewModel;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
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
        AppDatabase mDB = AppDatabase.GetMemDB(appContext);
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        AppDao mAppDao = mDB.appDao();
        new AppDatabase.InsertAsyncTask(mAppDao).execute(new Session(0,"20180101","20190102","this is a test"));

        for (int i=0;i<5;i++)
        {
            new AppDatabase.InsertAsyncTask(mAppDao).execute(new IntervalData(0,1,"test "+i));
        }


        new AppDatabase.InsertAsyncTask(mAppDao).execute(new Template(0,"Template 1","Example Template"));

        for (int i=0;i<5;i++)
        {
            new AppDatabase.InsertAsyncTask(mAppDao).execute(new Interval(0,"20;minutes",0));
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
        AppDatabase mDB = AppDatabase.GetMemDB(appContext);
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        AppDao mAppDao = mDB.appDao();

        List<Session> mSessions = mAppDao.getAllSessions();
        List<IntervalData> mData = mAppDao.getAllIntervalData();
        List<Template> mTemplate = mAppDao.getAllTemplates();
        List<Interval> mIntervalTypes = mAppDao.getAllIntervals();
    }

}
