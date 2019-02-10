package joqu.intervaltrainer.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.IntervalData;
import joqu.intervaltrainer.model.Session;


public class SavedSessionViewModel extends AndroidViewModel {
    AppDao mDao;
    private Session mSession;
    private LiveData<List<IntervalData>> mIntervalData;

    public SavedSessionViewModel(@NonNull Application application, int mSessionId) {
        super(application);
        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.GetDB(application.getApplicationContext());
        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        mDao = mDB.appDao();
        mSession = mDao.getSessionById(mSessionId);
        mIntervalData = mDao.getIntervalDataBySessionId(mSessionId);

    }


    public Session getSession() {
        return mSession;
    }

    public void saveSession(Session session) {
        new AppDatabase.InsertAsyncTask(mDao).execute(session);
    }

    public LiveData<List<IntervalData>> getIntervalData() {
        return mIntervalData;
    }

    public void saveIntervalData(IntervalData intervalData) {
        // TODO: Implement generic insert asynctask
        //new AppDatabase.InsertAsyncTask(mIntervalDataDao).execute(intervalData);
    }

}
