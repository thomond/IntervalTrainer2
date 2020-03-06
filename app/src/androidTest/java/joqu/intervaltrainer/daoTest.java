package joqu.intervaltrainer;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Random;

import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.SavedSession;
import joqu.intervaltrainer.model.SessionTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class daoTest {
    AppDao mAppDao;
    AppDatabase mDB;

    private void populateDB() {
        mDB.clearAllTables();
        try {
            SessionTemplate templ = new SessionTemplate("Debug Test Session", "Test", "Test");
            for (int i = 0; i <= 5; i++)
                templ.addInterval(new Random().nextInt(2), new Random().nextInt(10) * 1000, i);
            templ.saveAll(mDB.appDao());

            SessionTemplate templ2 = new SessionTemplate("Long Test Session", "Test", "Test");
            for (int i = 0; i <= 5; i++) templ2.addInterval(new Random().nextInt(2), 60000 * 8, i);
            templ2.saveAll(mDB.appDao());


            SavedSession sess = new SavedSession();
            sess.init("Test", templ.getId());
            sess.getSession().addLocations("48.8583,2.2944;48.8583,2.2946;48.8583,2.2970;48.8583,2.2949;");
            for (int i = 0; i <= 5; i++) {
                sess.addIntervalData(templ.getInterval(i));
                sess.finaliseInterval();
            }
            sess.saveAll(mDB.appDao());

        } catch (Exception e) {
            Log.e(Const.TAG, "");
        }
};

    @Before
    public void createDb(){
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("joqu.intervaltrainer", appContext.getPackageName());
        // Get an instance of the App Database and send the population callback
        mDB = AppDatabase.getInMemDB(appContext, null);
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        mAppDao = mDB.appDao();

    }

    @After
    public void closeDb() throws IOException {
        mDB.close();
    }

    @Test
    public void populateTest() throws InterruptedException {
        Boolean done = false;
        SessionTemplate sessTmpl = new SessionTemplate();
        // Wait for possibly a little time for the test to be written
        Thread.sleep(20000);
        sessTmpl.getFromDB(mAppDao,"Debug Test Session");
        Log.i(Const.APP_NAME, "daoTest: "+sessTmpl.print());


    }




}
