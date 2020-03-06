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

    Session mSession;


    LinkedList<IntervalData> mIntervalData;
    IntervalData mCurrentInterval;


    public SavedSession() {
        mSession = new Session();
        mIntervalData = new LinkedList();
    }

    public SavedSession(Session s, List<IntervalData> d) {
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

    public boolean saveAll(AppDao dao){
        new AppDatabase.InsertAsyncTask(dao).execute(mSession, mIntervalData);
        return true;
    }

    public void init(String title, int templateID){
        mSession.templateId = templateID;
        mSession.started = Util.getDateLong();
        mSession.title = title; //"Session on: " + new SimpleDateFormat("EEE, d MMM yyyy
    }
    public boolean finalise(){
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

    public void addIntervalData(Interval interval){
        IntervalData intervalData = new IntervalData(interval.id,mSession.id,interval.step);
        mIntervalData.push(intervalData);
        initInterval();
    }

    public void initInterval(){
        mCurrentInterval = mIntervalData.pop();
        mCurrentInterval.started = Util.getDateLong();

    }

    public void finaliseInterval(){
        // TODO: add pace etc
        mCurrentInterval.ended = Util.getDateLong();
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
