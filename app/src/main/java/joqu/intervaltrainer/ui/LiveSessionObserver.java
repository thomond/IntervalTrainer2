package joqu.intervaltrainer.ui;

import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.Util;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.LiveSession;

// Create the observer which updates the UI.
public class LiveSessionObserver implements Observer<LiveSession> {
    TextView liveSession_location,
             liveSession_speed,
             liveSession_distance,
             liveSessionInterval_countdown,
             liveSessionInterval_type,
             liveSessionInterval_total;
    MapView  liveSession_map;

    public LiveSessionObserver(View v) {
        liveSession_location = v.findViewById(R.id.liveSession_location);
        liveSession_speed = v.findViewById(R.id.liveSession_speed);
        liveSession_distance = v.findViewById(R.id.liveSession_distance);
        liveSessionInterval_countdown = v.findViewById(R.id.liveSessionInterval_countdown);
        liveSessionInterval_total = v.findViewById(R.id.liveSessionInterval_count);
        liveSessionInterval_type = v.findViewById(R.id.liveSessionInterval_type);
        liveSession_map = v.findViewById(R.id.liveSession_map);
    }

    @Override
    public void onChanged(@Nullable final LiveSession liveSession) {
        // FIXME: consider splitting this into own observer as object will chnage 1-2 times a second while location only changes every 5 seconds
        if(liveSession.location!=null){
            String loc = liveSession.location.getLatitude() + "," + liveSession.location.getLongitude();
            liveSession_location.setText(loc);
            liveSession_speed.setText(String.valueOf(liveSession.location.getSpeed()));

            // Add location data to map
            liveSession_map.getController().setCenter(new GeoPoint(liveSession.location));
            Marker marker = new Marker(liveSession_map) ;
            marker.setPosition(new GeoPoint(liveSession.location));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            if(!liveSession_map.getOverlayManager().contains(marker))
                liveSession_map.getOverlayManager().add(marker);
        }

        if(liveSession.distance!=null)
            liveSession_distance.setText(String.valueOf(liveSession.distance) + " m ");

        if(liveSession.timeLeft!=null){
            // Format timeleft to proper format   "mm:ss")
            String formattedTime = Util.millisToTimeFormat(liveSession.timeLeft,"mm:ss");
            liveSessionInterval_countdown.setText(formattedTime);
            liveSessionInterval_total.setText(liveSession.intervalIndex + "/" + liveSession.intervalTotal);


            liveSessionInterval_type.setText(IntervalData.getType(liveSession.intervalType));
        }

    }


}
