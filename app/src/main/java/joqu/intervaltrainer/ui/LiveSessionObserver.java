package joqu.intervaltrainer.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.Util;
import joqu.intervaltrainer.model.LiveSession;
import joqu.intervaltrainer.model.entities.IntervalData;

// Create the observer which updates the UI.
public class LiveSessionObserver implements Observer<LiveSession> {
    private TextView liveSession_location,
             liveSession_speed,
             liveSession_distance,
             liveSessionInterval_countdown,
             liveSessionInterval_type,
             liveSessionInterval_total;
    MapView  liveSession_map;
    private Polyline mPolyLine;
    boolean first=true; // defines if first run

    public LiveSessionObserver(View v) {
        //liveSession_location = v.findViewById(R.id.liveSession_location);
        liveSession_speed = v.findViewById(R.id.session_speed);
        liveSession_distance = v.findViewById(R.id.session_distance);
        liveSessionInterval_countdown = v.findViewById(R.id.session_time);
        liveSessionInterval_total = v.findViewById(R.id.IntervalDataItem_Step);
        liveSessionInterval_type = v.findViewById(R.id.IntervalDataItem_type);
        liveSession_map = v.findViewById(R.id.Session_map);
    }

    @Override
    public void onChanged(@Nullable final LiveSession liveSession) {
        // FIXME: consider splitting this into own observer as object will chnage 1-2 times a second while location only changes every 5 seconds
        if(liveSession.location!=null){

            //String loc = liveSession.location.getLatitude() + "," + liveSession.location.getLongitude();
            //liveSession_location.setText(loc);
            liveSession_speed.setText(String.valueOf(liveSession.location.getSpeed()));

            // Add location data to map
            liveSession_map.getController().setCenter(new GeoPoint(liveSession.location));
            // Add a polyline between each poiunt
            if(mPolyLine==null) mPolyLine = new Polyline(liveSession_map);
            mPolyLine.addPoint(new GeoPoint(liveSession.location));
            liveSession_map.getOverlayManager().add(mPolyLine);

            // Add starting marker
            if (!first) {
            } else {
                Marker marker = new Marker(liveSession_map) ;
                marker.setPosition(new GeoPoint(liveSession.location));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                liveSession_map.getOverlayManager().add(marker);
            }
            first=false;
        }

        if(liveSession.distance!=null)
            liveSession_distance.setText(String.valueOf(liveSession.distance));

        if(liveSession.timeLeft!=null){
            // Format timeleft to proper format   "mm:ss")
            String formattedTime = Util.millisToTimeFormat(liveSession.timeLeft,"mm:ss");
            liveSessionInterval_countdown.setText(formattedTime);
            liveSessionInterval_total.setText(liveSession.intervalIndex + "/" + liveSession.intervalTotal);


            liveSessionInterval_type.setText(IntervalData.getType(liveSession.intervalType));
        }

    }


}
