package joqu.intervaltrainer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.model.LiveSession;

import static joqu.intervaltrainer.Const.BROADCAST_COUNTDOWN_DONE;
import static joqu.intervaltrainer.Const.BROADCAST_COUNTDOWN_UPDATE;
import static joqu.intervaltrainer.Const.BROADCAST_GPS_UPDATE;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_COUNTDOWN_INDEX_INT;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_COUNTDOWN_TOTAL_INT;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_COUNTDOWN_TYPE_INT;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_DIST_FLOAT;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_LAT_DOUBLE;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_LONG_DOUBLE;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_SPEED_FLOAT;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_TIMELEFT_LONG;

public class LiveSessionBroadcastReceiver extends BroadcastReceiver {
    private MutableLiveData<LiveSession> mLiveSession;
    private static LiveSessionBroadcastReceiver INSTANCE;

    public LiveSessionBroadcastReceiver() {
        mLiveSession = new MutableLiveData<>();
        mLiveSession.postValue(LiveSession.getInstance());
    }

    public static LiveSessionBroadcastReceiver getInstance() {
        if(INSTANCE==null)
            return new LiveSessionBroadcastReceiver();
        else return INSTANCE;
    }

    public LiveData<LiveSession> getLiveSession() {
        return mLiveSession;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == BROADCAST_GPS_UPDATE) {
            Double lLat = intent.getDoubleExtra(INTENT_EXTRA_GPS_LAT_DOUBLE, -1);
            Double lLong = intent.getDoubleExtra(INTENT_EXTRA_GPS_LONG_DOUBLE,-1);
            float lDist = intent.getFloatExtra(INTENT_EXTRA_GPS_DIST_FLOAT,0.0f);
            float lSpeed = intent.getFloatExtra(INTENT_EXTRA_GPS_SPEED_FLOAT,0f);
            LiveSession liveSession = mLiveSession.getValue();
            Location lLocation = new Location("local");
            lLocation.setLatitude(lLat);
            lLocation.setLongitude(lLong);
            lLocation.setSpeed(lSpeed);
            liveSession.location = lLocation;
            liveSession.distance=lDist;
            mLiveSession.postValue(liveSession);
            //Log.d(Const.TAG,String.valueOf("Longitude: "+ mLiveSession.longitude.getValue() + "Latitude: " + mLiveSession.latitude.getValue()));
        }
        /*
           Process the interval timer broadcast from service
            contains the seconds left in timer
            the type(in integer), current index of timer, and the total number of interval timers
         */
        else if (action == BROADCAST_COUNTDOWN_UPDATE) {
            LiveSession liveSession = mLiveSession.getValue();
            liveSession.timeLeft = intent.getLongExtra(INTENT_EXTRA_TIMELEFT_LONG, -1);
            liveSession.intervalType = intent.getIntExtra(INTENT_EXTRA_COUNTDOWN_TYPE_INT, -1);
            liveSession.intervalIndex = intent.getIntExtra(INTENT_EXTRA_COUNTDOWN_INDEX_INT, -1);
            liveSession.intervalTotal = intent.getIntExtra(INTENT_EXTRA_COUNTDOWN_TOTAL_INT, -1);
            mLiveSession.setValue(liveSession);
            Log.d(Const.TAG,String.valueOf("Time Left: "+ liveSession.timeLeft));
        }
        else if (action == BROADCAST_COUNTDOWN_DONE){
            Log.d(Const.TAG, "Countdown done");
        }

    }
}
