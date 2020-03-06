package joqu.intervaltrainer.model;

import androidx.lifecycle.MutableLiveData;
import android.location.Location;

/*
Livesession object which store realtime data retrieved from service
Can return Session object ready to be persisted

 */
public class LiveSession {
    public Long timeLeft;
    public Float distance;
    public Location location;
    public int intervalType, intervalIndex, intervalTotal; // Holds the interval type,index and total for display
    static LiveSession INSTANCE;


    public LiveSession() {

    }


    public static LiveSession getInstance() {
        if (INSTANCE == null)
            INSTANCE = new LiveSession();
        return INSTANCE;
    }
}
