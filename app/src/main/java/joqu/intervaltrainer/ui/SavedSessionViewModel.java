package joqu.intervaltrainer.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import joqu.intervaltrainer.model.AppDao;
import joqu.intervaltrainer.model.AppDatabase;
import joqu.intervaltrainer.model.Interval;
import joqu.intervaltrainer.model.IntervalData;
import joqu.intervaltrainer.model.Session;
import joqu.intervaltrainer.model.Template;

import static android.content.ContentValues.TAG;


public class SavedSessionViewModel extends AndroidViewModel {
    AppDao mDao;
    private List<Session> mSessions;
    private List<Template> mTemplates;
    private List<IntervalData> mIntervalData;
    private List<Interval> mIntervals;

    public SavedSessionViewModel(@NonNull Application application) {
        super(application);
        // Get an instance of the App Database
        AppDatabase mDB = AppDatabase.GetDB(application.getApplicationContext());
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

}
