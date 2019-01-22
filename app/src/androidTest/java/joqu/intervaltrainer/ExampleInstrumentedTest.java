package joqu.intervaltrainer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.PrecomputedText;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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
    public void daoTest() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("joqu.intervaltrainer", appContext.getPackageName());
        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.GetDB(appContext);
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        SessionDao mSessionDao = mDB.sessionDao();
        new AppDatabase.InsertAsyncTask(mSessionDao).execute(new Session(0,"20180101","20190101","this is a test"));
        Thread.sleep(1000);
        List<Session> mSessions = mSessionDao.getAllSessions();
    }

}
