package joqu.intervaltrainer.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Date;
import java.util.List;

/*
Livesession object which store realtime data retrieved from service
Can return Session object ready to be persisted

 */
public class LiveSession {
    public MutableLiveData<Double> latitude;
    public MutableLiveData<Double> longitude;
    public MutableLiveData<Long> timeLeft;
    public MutableLiveData<Float> distance;
    public MutableLiveData<Float> speed;
    public MutableLiveData<Location> mLocation;
    private List<Location> tracks;
    static LiveSession INSTANCE;

    public LiveSession() {
        this.latitude = new MutableLiveData();
        this.longitude = new MutableLiveData();
        this.timeLeft = new MutableLiveData();
        this.mLocation = new MutableLiveData();
        this.distance = new MutableLiveData();
        this.timeLeft.postValue(Long.valueOf(0));
    }


    public static LiveSession getInstance() {
        if (INSTANCE == null)
            INSTANCE = new LiveSession();
        return INSTANCE;
    }
}
