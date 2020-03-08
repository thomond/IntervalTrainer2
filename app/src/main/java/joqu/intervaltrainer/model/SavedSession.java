package joqu.intervaltrainer.model;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import joqu.intervaltrainer.Util;
import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.entities.Session;

import static joqu.intervaltrainer.Const.TAG;

/**
 * Class to encapsulate data entities for a saved session
 *
 */
public class SavedSession
{
    public Session getSession() {
        return mSession;
    }

    public IntervalData getCurrentInterval() {
        return mCurrentInterval;
    }

    private Session mSession;


    private LinkedList<IntervalData> mIntervalData;
    private IntervalData mCurrentInterval;



    public SavedSession() {
        mSession = new Session();
        mIntervalData = new LinkedList();
    }

    private SavedSession(Session s, List<IntervalData> d) {
        mSession = s;
        mIntervalData = new LinkedList(d);;
    }

    /*
        Retrives list of saved sessions from DAO
     */
    public static List<SavedSession> getAllSessions(final AppDao dao){
        try {
            return (List<SavedSession>) new AsyncTask() {
                @Override
                protected List doInBackground(Object[] objects) {
                    LinkedList savedSessions = new LinkedList();
                    List<Session> sessions =  (List)dao.getAllSessions();
                    for (Session session :
                            sessions) {
                        List<IntervalData> data = dao.getIntervalDataBySessionId(session.id);
                        savedSessions.add(new SavedSession(session,data));
                    }
                    return (List)savedSessions;
                }
            }.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Persist all data
    public boolean saveAll(AppDao dao){
        new AppDatabase.InsertAsyncTask(dao).execute(mSession, mIntervalData);
        return true;
    }

    public void init(String title, int templateID){
        mSession.templateId = templateID;
        mSession.started = Util.getDateLong();
        mSession.title = title; //"Session on: " + new SimpleDateFormat("EEE, d MMM yyyy
    }

    // Performs session final calculations and persists all interval data to DB
    public boolean finalise(){
        // TODO: add average pace and average speed for full session
        for (IntervalData i : mIntervalData) {
            mSession.avgSpeed += i.avgSpeed;
        }
        mSession.avgSpeed = mSession.avgSpeed / mIntervalData.size();
        mSession.ended = Util.getDateLong();
        return true;
    }

    public boolean save(){return false;}

    public void addIntervalData(IntervalData data){mIntervalData.add(data);}

    public void addIntervalData(int intervalID,int step){
        IntervalData intervalData = new IntervalData(intervalID,mSession.id,step);
        mIntervalData.push(intervalData);
        initInterval();
    }

    // Entrypoint for new Activity Interval.
    public void addIntervalData(Interval interval){
        IntervalData intervalData = new IntervalData(interval.id,mSession.id,interval.step);
        mIntervalData.push(intervalData);
        initInterval();
    }

    private void initInterval(){
        mCurrentInterval = mIntervalData.pop();
        mCurrentInterval.started = Util.getDateLong();

    }

    // Performs final calculations and adds finished data to list for eventual persistence to DB
    public void finaliseInterval(){
        // TODO: add average pace and average speed for iinterval
        mCurrentInterval.ended = Util.getDateLong();
        mCurrentInterval.avgSpeed = mCurrentInterval.getAvgSpeed();
        mIntervalData.addLast(mCurrentInterval);


    }

    public String print(){
        StringBuffer buff = new StringBuffer();
        buff.append(mSession.title+"\n");
        buff.append(mSession.description+"\n");
        buff.append(mSession.data+"\n");
        buff.append(mSession.distance + " " + mSession.avgSpeed+"\n");
        buff.append("Intervals:"+"\n");
        for (IntervalData i :
                mIntervalData) {
            buff.append(i.id+"   ");
            buff.append(i.data+"\t");
        }
        buff.append("\n");


        return buff.toString();
    }
}
