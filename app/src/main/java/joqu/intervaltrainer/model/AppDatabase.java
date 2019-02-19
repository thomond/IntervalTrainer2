package joqu.intervaltrainer.model;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.List;

import static android.content.ContentValues.TAG;

// Set the entity (table) names and versions number
@Database(entities = {Session.class, IntervalData.class, Template.class, Interval.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    private static volatile AppDatabase DB = null;
    private static String DBName = "DEBUG";
    // Retrive DAO for entities
    public abstract AppDao appDao();
    //public abstract IntervalDataDao IntervalDataDao();
    // Singleton func to get DB or create if it doesn't exist
    public static AppDatabase GetDB(final Context context ){
        if (DB == null){
            synchronized (AppDatabase.class){
                if (DB == null){

                    DB = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DBName).addCallback(mAppDatabaseCallback).build();

                }
            }
        }
        return DB;
    }

    public static AppDatabase GetMemDB(final Context context ){
        if (DB == null){
            synchronized (AppDatabase.class){
                if (DB == null){
                    if (DBName=="DEBUG") {
                        context.deleteDatabase(DBName);
                        DB = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class).addCallback(mAppDatabaseCallback).build();
                    }else
                        DB = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,DBName).addCallback(mAppDatabaseCallback).build();

                }
            }
        }
        return DB;
    }

    // onOpen callback for database
    private static Callback
        mAppDatabaseCallback = new Callback() {
            // Tasks to complete on each DB open
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);

                new PopulateAppDbAsyncTask(DB).execute();
            }
        };




    // async task to initiates the content of database
    // TODO: check if tables already exist/ are populated
    public static class PopulateAppDbAsyncTask extends AsyncTask<Void, Void, Void> {

        AppDao mDAO;
        public PopulateAppDbAsyncTask(AppDatabase DB) {
            mDAO = DB.appDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            if (DBName=="DEBUG"){
                DB.clearAllTables();

                Template mTem = new Template(1,"Test Template","test","Test Template");
                mDAO.addTemplate(mTem);
                for (int i=0;i<5;i++)
                {
                    mDAO.addInterval(new Interval(0,"",mTem.id));
                }


                // mDAO.deleteSessions();
                Session mSess = new Session(1,0,"20190101 00:00","20190101 00:01","test, test");

                mDAO.addSession(mSess);
                for (int i=0;i<5;i++)
                {
                    mDAO.addIntervalData(new IntervalData(i,0,mSess.id,"test "+i));
                }

            }


            return null;
        }
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
                if (params[0] instanceof Session)
                    rows = mDAO.addSession((Session)params[0]);
                else if (params[0] instanceof Template)
                    rows = mDAO.addTemplate((Template)params[0]);
                else if (params[0] instanceof Interval)
                    rows = mDAO.addInterval((Interval)params[0]);
                else if (params[0] instanceof IntervalData)
                    rows = mDAO.addIntervalData((IntervalData)params[0]);
                else return new Long(-1);
            } catch (NullPointerException e) {
                Log.e(TAG, "InsertAsyncTask.doInBackground: params is NULL");
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
        protected List doInBackground(final Object... params) {

            try {
                if (params[0] == Session.class)
                    return mDAO.getAllSessions();
                else if (params[0] == Template.class)
                    return mDAO.getAllTemplates();
                else if (params[0] == Interval.class)
                    return mDAO.getAllIntervals();
                else if (params[0] == IntervalData.class)
                    return mDAO.getAllIntervalData();
                else return Collections.emptyList();
            } catch (NullPointerException e) {
                Log.e(TAG, "SelectAsyncTask.doInBackground: params is NULL");
            }
            return Collections.emptyList();
        }
    }
}