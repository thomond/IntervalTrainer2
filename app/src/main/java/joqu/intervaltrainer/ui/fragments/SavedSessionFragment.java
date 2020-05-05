package joqu.intervaltrainer.ui.fragments;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.R;
import joqu.intervaltrainer.Util;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.entities.Session;
import joqu.intervaltrainer.ui.AppViewModel;
import joqu.intervaltrainer.ui.ItemClickListener;
import joqu.intervaltrainer.ui.adapters.IntervalDataAdapter;
import joqu.intervaltrainer.ui.adapters.SessionListAdapter;

import static android.content.ContentValues.TAG;

// TODO: generalise fragment to also show selected session
public class SavedSessionFragment extends Fragment implements ItemClickListener {


    private AppViewModel mViewModel;
    private int mSessionSelected; // The session id selelcted via the session list to be presented
    private RecyclerView mSessionDataRecyclerView;
    private  View mSessionView;
    private MapView mMapView;
    private RecyclerView intervalDataRecyclerView;
    private TextView sessionTimeText;
    private TextView sessionDistanceText;
    private TextView sessionPaceText;
    private TextView sessionSpeedText;
    private TextView sessionTitleText;
    private Polyline mPolyLine;

    public static SavedSessionFragment newInstance() {
        return new SavedSessionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_session, container, false);
        mSessionView = v.findViewById(R.id.ViewSavedSession);
        mSessionDataRecyclerView = v.findViewById(R.id.sessionListRecyclerView);
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        // SavedSession elements
        sessionTitleText = v.findViewById(R.id.session_title);
        sessionDistanceText = v.findViewById(R.id.session_distance);
        sessionTimeText = v.findViewById(R.id.sessionListItem_time);
        sessionPaceText = v.findViewById(R.id.session_pace);
        sessionSpeedText = v.findViewById(R.id.session_speed);
        intervalDataRecyclerView = v.findViewById(R.id.intervalDataView);

        mMapView = v.findViewById(R.id.SavedSession_map);

        showSessionList();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void showSessionList() {
        mSessionDataRecyclerView.setVisibility(View.VISIBLE);
        mSessionView.setVisibility(View.GONE);

        // Populate recycler with  items only if empty
        if (mSessionDataRecyclerView.getAdapter()==null) {
            final SessionListAdapter mAdapter = new SessionListAdapter(getContext(), this);
            mSessionDataRecyclerView.setAdapter(mAdapter);
            mSessionDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter.setSessionList(mViewModel.getAllSessions(), mViewModel.getAllTemplates());
        }
    }

    private void showSession(int sessionID) {
        // Hide session list and show session
        mSessionDataRecyclerView.setVisibility(View.GONE);
        mSessionView.setVisibility(View.VISIBLE);
        try {
            // Session information
            Session msess = mViewModel.getSessionById(sessionID);
            List<IntervalData> mIntervalData = mViewModel.getSessionIntervalData(sessionID);

            mMapView.setTileSource(TileSourceFactory.OpenTopo);
            // Center map on location
            IMapController mapController = mMapView.getController();
            mapController.setZoom(18.0);
            // Aquire locations from Intervals and add locations data to map
            List<Location> locations = new LinkedList<>();
            for (IntervalData i :
                    mIntervalData) {
                locations.addAll(i.getLocations());
            }


            mapController.setCenter(new GeoPoint(locations.get(0)));
            for (Location l :
                    locations) {
                // Add a polyline between each poiunt
                if(mPolyLine==null) mPolyLine = new Polyline(mMapView);
                mPolyLine.addPoint(new GeoPoint(l));
                mMapView.getOverlayManager().add(mPolyLine);

                Marker marker = new Marker(mMapView);
                marker.setPosition(new GeoPoint(l));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlayManager().add(marker);
            }

            // Set session elements
            sessionTitleText.setText(msess.title);

            long time = Util.millisBetween(msess.started, msess.ended);

            sessionTimeText.setText(Util.millisToTimeFormat(time, "mm:ss"));
            sessionDistanceText.setText(String.format(Locale.getDefault(), "%.2f", msess.distance));
            sessionSpeedText.setText(String.format(Locale.getDefault(), "%d m/s", msess.avgSpeed));
            sessionPaceText.setText(String.format(Locale.getDefault(), "%d sec/m", msess.pace));

            // Populate recycler for IntervalData items
            final IntervalDataAdapter mAdapter = new IntervalDataAdapter(getContext());
            mAdapter.setIntervalData(mViewModel.getSessionIntervalData(sessionID), mViewModel.getTemplateIntervals(msess.templateId));
            intervalDataRecyclerView.setAdapter(mAdapter);
            intervalDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        }catch(Exception e){
            Log.e(Const.TAG,e.getMessage());
        }
    }
    @Override
    public void onItemClick(View view, int position) {
        this.mSessionSelected = position;
        Log.i(TAG,"Click From View: "+view.toString());
        // TODO switch view to saved session based on ID
        showSession(this.mSessionSelected);

    }
}
