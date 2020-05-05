package joqu.intervaltrainer.model;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.Util;
import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.entities.Session;

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

    public SavedSession(Session s, List<IntervalData> d) {
        mSession = s;
        mIntervalData = new LinkedList(d);;
    }

    // Takes a string of Locations delimited by ";" and calls addNewLocation to parse each location string
    public void addNewLocations(String locationsStr){
        try {
            String[] locationData = locationsStr.split(";");
            for (String location : locationData
            ) {
                addNewLocation(location);
            }
        }catch (Exception e){
            Log.e(Const.TAG,e.getMessage());
            throw e;
        }

    }

    //Takes a string in format of lat,long,speed and creates a Locatation form this and then calls the regular addnew location
    public void addNewLocation(String locationStr){
        String[] locationData = locationStr.split(",");
        Location location = new  Location("Debug");
        try {
            double _lat = Double.parseDouble(locationData[0]);
            double _long = Double.parseDouble(locationData[1]);
            location.setLatitude(_lat);
            location.setLongitude(_long);
            if(locationData.length>2)
                location.setSpeed(Float.parseFloat(locationData[2]));
            else location.setSpeed(0);

            addNewLocation(location);
        }catch (Exception e){
            Log.e(Const.TAG,e.getMessage());
            throw e;
        }
    }

    //Adds new location object to session
    public void addNewLocation(Location location){
        // Update session object and current interval
        //getSession().addLocation(location);
        getCurrentInterval().addLocation(location);
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
        // TODO: add average pace for full session
        for (IntervalData i : mIntervalData) {
            mSession.avgSpeed += i.avgSpeed;
            mSession.distance += i.distance;
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

    public String getString(){
        StringBuilder buff = new StringBuilder()
                .append("════════════════════════════════\n")
                .append(mSession.title).append("\n")
                .append("║  Desc.: ")
                .append(mSession.description).append("\n")
                .append("║  Data: ")
                .append(mSession.data).append("\n")
                .append("║  Distance: ")
                .append(mSession.distance).append("\n")
                .append("║  Speed: ")
                .append(mSession.avgSpeed).append("\n")
                .append("║ Locations: ")
                .append(mSession.locationData).append("\n")
                .append("║ Intervals:"+"\n");
        for (IntervalData i :
                mIntervalData) {
            buff.append("═══════ ID: ").append(i.id).append("\n")
                    .append("║ Step: ").append(i.step).append("\n")
                    .append("║ Locations: ").append(i.locationData).append("\n")
                    .append("║ Distance: ").append(i.distance).append("\n")
                    .append("║ Started: ").append(i.started).append("\n")
                    .append("║ Ended: ").append(i.ended).append("\n")
                    .append("════════════════ ").append("\n");
        }
        buff.append("════════════════════════════════\n");



        return buff.toString();
    }
}
