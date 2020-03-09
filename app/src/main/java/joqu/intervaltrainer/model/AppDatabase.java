package joqu.intervaltrainer.model;


import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

import joqu.intervaltrainer.BuildConfig;
import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.entities.Session;
import joqu.intervaltrainer.model.entities.Template;


// Set the entity (table) names and versions number
@Database(entities = {Session.class, IntervalData.class, Template.class, Interval.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    private static volatile AppDatabase DB = null;
    private static String DBName = "IntervalTrainer";
    // Retrive DAO for entities
    public abstract AppDao appDao();


    public static AppDatabase getDB(final Context context) {
        if(DB==null) {
            try {
                synchronized (AppDatabase.class) {
                    context.deleteDatabase(DBName);
                    DB  = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DBName).build();
                    new Thread(() -> {
                        if(DB.appDao().getAllTemplates().isEmpty())
                            // Populate the DB just created
                            AppDatabase.populateDB();
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //
        return DB;
    }

    public static AppDatabase getInMemDB(final Context context) {
        if(DB==null) {
            try {
                synchronized (AppDatabase.class) {
                    context.deleteDatabase(DBName);
                    DB = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class).build();
                    // Populate the DB just created
                    AppDatabase.populateDB();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //DB = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DBName).build();
        return DB;
    }

    // Changed this froma  asynctask to a method as having multiple threads try to read/write to DB caused a deadlock
    @NonNull
    public static Boolean populateDB() {
            try {
                SessionTemplate templ = new SessionTemplate("Debug Test Session", "Test", "Test");
                for (int i = 0; i <= 5; i++) templ.addInterval(new Random().nextInt(2), new Random().nextInt(10) * 1000, i);
                templ.saveAll(DB.appDao());

                SessionTemplate templ2 = new SessionTemplate("Long Test Session", "Test", "Test");
                for (int i = 0; i <= 5; i++) templ2.addInterval(new Random().nextInt(2), 60000*8, i);
                templ2.saveAll(DB.appDao());


                SavedSession sess = new SavedSession();
                sess.init("Test", templ.getId());
                sess.getSession().addLocations("48.8583,2.2944;48.8583,2.2946;48.8583,2.2970;48.8583,2.2949;");
                for (int i = 0; i <= 5; i++) {
                    sess.addIntervalData(templ.getInterval(i));
                    sess.finaliseInterval();
                }
                sess.saveAll(DB.appDao());

            } catch (Exception e) {
                Log.e(Const.TAG,"");
            }
        return true;
    }

    // async task for inserts into the DB
    public  static class InsertAsyncTask extends AsyncTask<Object, Void, Long> {

        AppDao mDAO;

        public InsertAsyncTask(AppDao mDAO) {
            this.mDAO = mDAO;
        }


        @Override
        protected Long doInBackground(final Object... params) {
            long rows = 0;
            try {
                for (Object param :
                        params) {
                    if (param instanceof Session)
                        rows = mDAO.addSession((Session)param);
                    else if (param instanceof Template)
                        rows = mDAO.addTemplate((Template)param);
                    else if (param instanceof Interval)
                        rows = mDAO.addInterval((Interval)param);
                    else if (param instanceof IntervalData)
                        rows = mDAO.addIntervalData((IntervalData)param);
                    else if (param instanceof List){
                        for (Object item :
                                (List)param) {
                            if (item instanceof Session)
                                rows = mDAO.addSession((Session)item);
                            else if (item instanceof Template)
                                rows = mDAO.addTemplate((Template)item);
                            else if (item instanceof Interval)
                                rows = mDAO.addInterval((Interval)item);
                            else if (item instanceof IntervalData)
                                rows = mDAO.addIntervalData((IntervalData)item);
                         }
                        }else return new Long(-1);
                    }
            } catch (NullPointerException e) {
                Log.e(Const.TAG, "InsertAsyncTask.doInBackground: params is NULL");
            }
            return rows;
        }
    }

    // async task for inserts into the DB
    public  static class SelectAsyncTask extends AsyncTask<Object, Void, List> {

        AppDao mDAO;

        public SelectAsyncTask(AppDao mDAO) {
            this.mDAO = mDAO;
        }

        @Override
        protected void onPostExecute(List output){ return; }

        @Override
        protected List doInBackground(final Object... params) {

            try {
                for (Object param :
                        params) {
                    if (param == Session.class)
                        return mDAO.getAllSessions();
                    else if (param == Template.class)
                        return mDAO.getAllTemplates();
                    else if (param == Interval.class)
                        return mDAO.getAllIntervals();
                    else if (param == IntervalData.class)
                        return mDAO.getAllIntervalData();
                    for (Object item :
                            (List)param) {
                        if (item == Session.class)
                            return mDAO.getAllSessions();
                        else if (item == Template.class)
                            return mDAO.getAllTemplates();
                        else if (item == Interval.class)
                            return mDAO.getAllIntervals();
                        else if (item == IntervalData.class)
                            return mDAO.getAllIntervalData();
                    }
                }
            } catch (NullPointerException e) {
                Log.e(Const.TAG, "SelectAsyncTask.doInBackground: params is NULL");
            }
            return Collections.emptyList();
        }
    }


}
