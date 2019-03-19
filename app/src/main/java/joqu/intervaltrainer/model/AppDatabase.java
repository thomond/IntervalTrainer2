package joqu.intervaltrainer.model;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Random;

import joqu.intervaltrainer.BuildConfig;
import joqu.intervaltrainer.Const;




// Set the entity (table) names and versions number
@Database(entities = {Session.class, IntervalData.class, Template.class, Interval.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    private static volatile AppDatabase DB = null;
    private static String DBName = "IntervalTrainer";
    // Retrive DAO for entities
    public abstract AppDao appDao();
    //public abstract IntervalDataDao IntervalDataDao();
    // Singleton func to get DB or create if it doesn't exist
    public static AppDatabase getDB(final Context context ){
        if (DB == null){
            synchronized (AppDatabase.class){
                    if (BuildConfig.DEBUG) {
                        context.deleteDatabase(DBName);
                        DB = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class).addCallback(mAppDatabaseCallback).build();
                    }
                    else
                        DB = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DBName).addCallback(mAppDatabaseCallback).build();
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
                if(BuildConfig.DEBUG){
                    // Populate DB with test data
                    new AppDatabase.PopulateAppDbAsyncTask(DB).execute();
                }

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
            if (BuildConfig.DEBUG) {
                DB.clearAllTables();

                Template mTem = new Template("Test Template","test","Test Template");
                Log.i(Const.TAG, mTem.hashCode()+ " added");
                mDAO.addTemplate(mTem);
                for (int i=0;i<5;i++)
                {
                    long rand = new Random().nextInt(5)*1000;
                    Interval interval = new Interval(0,""+rand,1,i);
                    mDAO.addInterval(interval);
                    Log.i(Const.TAG, "Interval"+ i +" added: "+ interval.toString());
                }


                // mDAO.deleteSessions();
                Session mSess = new Session(0,Long.toString(System.currentTimeMillis()),Long.toString(System.currentTimeMillis()),"test, test");
                Log.i(Const.TAG, mSess.hashCode()+ " added");
                mDAO.addSession(mSess);
                for (int i=0;i<5;i++)
                {
                    mDAO.addIntervalData(new IntervalData(0,1,"test "+i));
                    Log.i(Const.TAG, i + " IntervalData added");
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
                Log.e(Const.TAG, "SelectAsyncTask.doInBackground: params is NULL");
            }
            return Collections.emptyList();
        }
    }


}