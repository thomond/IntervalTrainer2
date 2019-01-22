package joqu.intervaltrainer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;



public class SessionViewModel extends AndroidViewModel {
    SessionDao mSessionDao;
    private LiveData<List<Session>> mSessions;
    IntervalDataDao mIntervalDataDao;
    private LiveData<List<IntervalData>> mIntervalData;

    public SessionViewModel(@NonNull Application application, int mSessionId) {
        super(application);
        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.GetDB(application.getApplicationContext());
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        mSessionDao = mDB.sessionDao();
        mSessions = mSessionDao.getAllSessions();

        // Aquire a DAO instance from the database and retrieve all IntervalDatas present etc.
        mIntervalDataDao = mDB.IntervalDataDao();
        mIntervalData = mIntervalDataDao.getIntervalDataBySessionId(mSessionId);

    }


    public LiveData<List<Session>> getSessions() {
        return mSessions;
    }

    public void saveSession(Session session) {
        new AppDatabase.InsertAsyncTask(mSessionDao).execute(session);
    }

    public LiveData<List<IntervalData>> getIntervalData() {
        return mIntervalData;
    }

    public void saveIntervalData(IntervalData intervalData) {
        // TODO: Implement generic insert asynctask
        //new AppDatabase.InsertAsyncTask(mIntervalDataDao).execute(intervalData);
    }

}
