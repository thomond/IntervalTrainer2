package joqu.intervaltrainer.model;

import android.location.Location;

import java.util.Date;
import java.util.List;

/*
Livesession object which store realtime data retrieved from service
Can return Session object ready to be persisted

 */
public class LiveSession {
    private Location mLocation;
    private List<Location> track;
    Date startTime;
    int secondsLeft;
    double distanceTraveled;
    double velocity;
    Template mTemplate;
    List<Interval> mTemplateIntervals;
    Session mSession;

    public LiveSession(Template mTemplate, List mTemplateIntervals) {
        this.mTemplate = mTemplate;
        this.mTemplateIntervals = mTemplateIntervals;
    }





    Session getSession(){
        // TODO: populate session object
        return mSession;
    }

}
