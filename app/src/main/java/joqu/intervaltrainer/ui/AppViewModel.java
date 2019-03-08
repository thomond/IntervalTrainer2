package joqu.intervaltrainer.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import joqu.intervaltrainer.BuildConfig;
import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.Interval;
import joqu.intervaltrainer.model.IntervalData;
import joqu.intervaltrainer.model.LiveSession;
import joqu.intervaltrainer.model.Session;
import joqu.intervaltrainer.model.Template;

import static android.content.ContentValues.TAG;
import static joqu.intervaltrainer.Const.BROADCAST_COUNTDOWN_DONE;
import static joqu.intervaltrainer.Const.BROADCAST_COUNTDOWN_UPDATE;
import static joqu.intervaltrainer.Const.BROADCAST_GPS_UPDATE;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_LAT_DOUBLE;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_LONG_DOUBLE;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_TIMELEFT_LONG;


public class AppViewModel extends AndroidViewModel {
    AppDao mDao;
    private List<Session> mSessions;
    private List<Template> mTemplates;
    private List<IntervalData> mIntervalData;
    private List<Interval> mIntervals;
    private LiveSession mLiveSession;

    // set up BroadcastReciever for data updates from service
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == BROADCAST_GPS_UPDATE) {
                Double mLat = intent.getDoubleExtra(INTENT_EXTRA_GPS_LAT_DOUBLE, -1);
                Double mLong = intent.getDoubleExtra(INTENT_EXTRA_GPS_LONG_DOUBLE,-1);
                mLiveSession.latitude.postValue(mLong);
                mLiveSession.longitude.postValue(mLat);
                Log.d(Const.TAG,String.valueOf("Longitude: "+ mLiveSession.longitude.getValue() + "Latitude: " + mLiveSession.latitude.getValue()));
            }
            else if (action == BROADCAST_COUNTDOWN_UPDATE) {
                long mTimeleft = intent.getLongExtra(INTENT_EXTRA_TIMELEFT_LONG, -1);
                mLiveSession.timeLeft.postValue(mTimeleft);
                Log.d(Const.TAG,String.valueOf("Time Left: "+ mLiveSession.timeLeft.getValue()));
            }
            else if (action == BROADCAST_COUNTDOWN_DONE){
                Log.d(Const.TAG, "Countdown done");
            }
        }
    };


    public AppViewModel(@NonNull Application application) {
        super(application);
        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.GetDB(application.getApplicationContext());
        if(BuildConfig.DEBUG){
            // Populate DB with test data
            new AppDatabase.PopulateAppDbAsyncTask(mDB).execute();
        }

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

        // Register the relevant broadcast receivers
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_GPS_UPDATE);
        intentFilter.addAction(Const.BROADCAST_COUNTDOWN_UPDATE);
        intentFilter.addAction(Const.BROADCAST_COUNTDOWN_DONE);
        LocalBroadcastManager.getInstance(application.getApplicationContext()).registerReceiver(mReceiver,intentFilter);

        mLiveSession = LiveSession.getInstance();
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

    public LiveData<Double> getLiveLatitude() {
        return mLiveSession.latitude;
    }
    public LiveData<Double> getLiveLongitude() {
        return mLiveSession.longitude;
    }
    public LiveData<Long> getTimeLeft() {
        return mLiveSession.timeLeft;
    }
}
