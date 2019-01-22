package joqu.intervaltrainer;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.method.DateTimeKeyListener;
import android.util.Log;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static android.content.ContentValues.TAG;

// Set the entity (table) names and versions number
@Database(entities = {Session.class, IntervalData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    private static volatile AppDatabase DB = null;
    private static String DBName = "DEBUG";
    // Retrive DAO for entities
    public abstract SessionDao sessionDao();
    public abstract IntervalDataDao IntervalDataDao();
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

        SessionDao mDAO;
        public PopulateAppDbAsyncTask(AppDatabase DB) {
            mDAO = DB.sessionDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mDAO.deleteAll();
            mDAO.addSession(new Session(0,"20190101 00:00","20190101 00:01","test, test"));
            return null;
        }
    }


    // async task for inserts into the DB

    public  static class InsertAsyncTask extends AsyncTask<Session, Void, Long> {

        SessionDao mDAO;
        public InsertAsyncTask(SessionDao mDAO) {
            mDAO = mDAO;
        }

        @Override
        protected Long doInBackground(final Session... params) {
            long rows = 0;
            try {
                rows =  mDAO.addSession(params[0]);
            }catch( NullPointerException e){
                Log.e(TAG, "doInBackground: params is NULL");
            }
            return rows;
        }
    }

}