package joqu.intervaltrainer.ui;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.LiveSession;
import joqu.intervaltrainer.model.entities.Session;
import joqu.intervaltrainer.model.entities.Template;

import static android.content.ContentValues.TAG;


public class AppViewModel extends AndroidViewModel {
    AppDao mDao;
    private List<Session> mSessions;
    private List<IntervalData> mIntervalData;
    private List<Template> mTemplates;
    private List<Interval> mIntervals;


    // set up BroadcastReciever for data updates from service
    LiveSessionBroadcastReceiver mReceiver;


    public AppViewModel(@NonNull Application application) {
        super(application);
        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.getDB(application.getApplicationContext());


        // Aquire a DAO instance from the database and retrieve all sessions present etc.
        mDao = mDB.appDao();
        try {
            mSessions = new AppDatabase.SelectAsyncTask(mDao).execute(Session.class).get();
            // Get template info based on id saved in session
            mTemplates =  new AppDatabase.SelectAsyncTask(mDao).execute(Template.class).get();
            mIntervalData = new AppDatabase.SelectAsyncTask(mDao).execute(IntervalData.class).get();
            mIntervals =  new AppDatabase.SelectAsyncTask(mDao).execute(Interval.class).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mReceiver = LiveSessionBroadcastReceiver.getInstance();
        // Register the relevant broadcast receivers
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_GPS_UPDATE);
        intentFilter.addAction(Const.BROADCAST_COUNTDOWN_UPDATE);
        intentFilter.addAction(Const.BROADCAST_COUNTDOWN_DONE);
        LocalBroadcastManager.getInstance(application.getApplicationContext()).registerReceiver(mReceiver,intentFilter);


    }



    public Session getSessionById(int id) {
        for (Session s: mSessions){
            if (s.id == id) return s;
        }
        return null;
    }

    public Template getSessionTemplate(int tid){
        for (Template t: mTemplates){
            if (t.id == tid) return t;
        }
        return null;
    }


    public void saveSession(Session session) {
        new AppDatabase.InsertAsyncTask(mDao).execute(session);
    }

    public List getSessionIntervalData(int sessionId){
        List data = new ArrayList();
        try {
            for (IntervalData d: mIntervalData){
                if (d.sessionId == sessionId)
                    data.add(d);
            }
        }catch (UnsupportedOperationException e){
            Log.e(TAG, e.getMessage());

        }
        return data;
    }

    public Interval getIntervalById(int intervalId){
        for (Interval d: mIntervals){
            if (d.id == intervalId) return d;
        }
        return null;
    }

    public void saveIntervalData(IntervalData intervalData) {
        // TODO: Implement generic insert asynctask
        //new AppDatabase.InsertAsyncTask(mIntervalDataDao).execute(intervalData);
    }

    public List<Session> getAllSessions() {
        return mSessions;
    }

    public List<Template> getAllTemplates() {
        return mTemplates;
    }

    public List<Interval> getTemplateIntervals(int tid) {
        List data = new ArrayList();
        try {
            for (Interval d: mIntervals){
                if (d.templateID == tid)
                    data.add(d);
            }
        }catch (UnsupportedOperationException e){
            Log.e(TAG, e.getMessage());

        }
        return data;
    }


    public LiveData<LiveSession> getLiveSession() {  return mReceiver.getLiveSession();}


}
