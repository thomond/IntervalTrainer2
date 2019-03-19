package joqu.intervaltrainer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.model.LiveSession;

import static joqu.intervaltrainer.Const.BROADCAST_COUNTDOWN_DONE;
import static joqu.intervaltrainer.Const.BROADCAST_COUNTDOWN_UPDATE;
import static joqu.intervaltrainer.Const.BROADCAST_GPS_UPDATE;
import static joqu.intervaltrainer.Const.BROADCAST_SVC_STOPPED;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_DIST_FLOAT;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_LAT_DOUBLE;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_LONG_DOUBLE;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_GPS_SPEED_FLOAT;
import static joqu.intervaltrainer.Const.INTENT_EXTRA_TIMELEFT_LONG;

public class LiveSessionBroadcastReceiver extends BroadcastReceiver {
    private LiveSession mLiveSession;
    private static LiveSessionBroadcastReceiver INSTANCE;

    public LiveSessionBroadcastReceiver() {
        mLiveSession = LiveSession.getInstance();
    }

    public static LiveSessionBroadcastReceiver getInstance() {
        if(INSTANCE==null)
            return new LiveSessionBroadcastReceiver();
        else return INSTANCE;
    }

    public LiveSession getLiveSession() {
        return mLiveSession;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == BROADCAST_GPS_UPDATE) {
            Double lLat = intent.getDoubleExtra(INTENT_EXTRA_GPS_LAT_DOUBLE, -1);
            Double lLong = intent.getDoubleExtra(INTENT_EXTRA_GPS_LONG_DOUBLE,-1);
            float lDist = intent.getFloatExtra(INTENT_EXTRA_GPS_DIST_FLOAT,0);
            float lSpeed = intent.getFloatExtra(INTENT_EXTRA_GPS_SPEED_FLOAT,0);
            Location lLocation = new Location("local");
            lLocation.setLatitude(lLat);
            lLocation.setLongitude(lLong);
            lLocation.setSpeed(lSpeed);
            mLiveSession.mLocation.postValue(lLocation);
            mLiveSession.distance.postValue(lDist);
            //Log.d(Const.TAG,String.valueOf("Longitude: "+ mLiveSession.longitude.getValue() + "Latitude: " + mLiveSession.latitude.getValue()));
        }
        else if (action == BROADCAST_COUNTDOWN_UPDATE) {
            long lTimeleft = intent.getLongExtra(INTENT_EXTRA_TIMELEFT_LONG, -1);
            mLiveSession.timeLeft.postValue(lTimeleft);
            Log.d(Const.TAG,String.valueOf("Time Left: "+ mLiveSession.timeLeft.getValue()));
        }
        else if (action == BROADCAST_COUNTDOWN_DONE){
            Log.d(Const.TAG, "Countdown done");
        }

    }
}
