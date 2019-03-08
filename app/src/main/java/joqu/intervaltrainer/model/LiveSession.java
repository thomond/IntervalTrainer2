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
    private Location mLocation;
    private List<Location> track;
    static LiveSession INSTANCE;

    public LiveSession() {
        this.latitude = new MutableLiveData();
        this.longitude = new MutableLiveData();
        this.timeLeft = new MutableLiveData();
        this.latitude.postValue(0.0);
        this.longitude.postValue(0.0);
        this.timeLeft.postValue(Long.valueOf(0));
    }


    public static LiveSession getInstance() {
        if (INSTANCE == null)
            INSTANCE = new LiveSession();
        return INSTANCE;
    }
}
