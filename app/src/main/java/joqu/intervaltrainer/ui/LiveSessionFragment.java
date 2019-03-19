package joqu.intervaltrainer.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.text.SimpleDateFormat;
import java.util.Date;

import joqu.intervaltrainer.BuildConfig;
import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.LiveSessionService;
import joqu.intervaltrainer.R;

import static joqu.intervaltrainer.Const.BROADCAST_SVC_STARTED;
import static joqu.intervaltrainer.Const.BROADCAST_SVC_STOPPED;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveSessionFragment extends Fragment {

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action)
            {
                case BROADCAST_SVC_STOPPED:
                {
                    mServiceIsRunning = false;
                    if (mStartbutton!=null) mStartbutton.setText("Start");
                    showSessionList();
                    break;
                }
                case BROADCAST_SVC_STARTED:
                {
                    mServiceIsRunning = true;
                    if (mStartbutton!=null) mStartbutton.setText("Stop");
                    break;
                }
            }

        }
    };

    AppViewModel mAppViewModel;

    private boolean mServiceIsRunning = false;
    private Button mStartbutton;
    private MapView mMapView;
    private Observer<Location> locationObserver;

    public LiveSessionFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LiveSessionFragment.
     *
     */
    public static LiveSessionFragment newInstance() {
        return new LiveSessionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load/initialize the osmdroid configuration so tiles can be cached
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));


        // Register the svc state reciever
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_SVC_STOPPED);
        intentFilter.addAction(Const.BROADCAST_SVC_STARTED);
        intentFilter.addAction(Const.BROADCAST_SVC_PAUSED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver,intentFilter);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume views
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    // Stop any session currently running
    public void stopService(){
        Log.i(Const.TAG,"Sending signal to stop service...");
        LocalBroadcastManager
                .getInstance(getContext()).sendBroadcast(new Intent(Const.BROADCAST_SVC_STOP));
        //getContext().stopService( new Intent(getContext(), LiveSessionService.class));
    }

    public void pauseService() {
        // Send the Pause action
        Intent intent = new Intent(getContext(), LiveSessionService.class);
        intent.setAction(Const.BROADCAST_SVC_PAUSE);
        LocalBroadcastManager
                .getInstance(getContext()).sendBroadcast(intent);
    }

    public void startService(){
        int lTemplateID;
        if (BuildConfig.DEBUG){
            lTemplateID = 1;
        }else{
            // TODO: Add logic to show templatelist
            throw new UnsupportedOperationException("Not Implemented");

        }

        Intent intent = new Intent(getContext(), LiveSessionService.class);
        // Fill the intent with the temp id and start the service
        intent.putExtra(Const.INTENT_EXTRA_TEMPLATE_ID_INT,lTemplateID);
        // Start service
        if (Build.VERSION.SDK_INT >= 26) {
            // Start in foreground; id: non-zro identifier, Notification object to show in taskbar
            getContext().startForegroundService(intent);
        }else
            getContext().startService(intent);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View lView =  inflater.inflate(R.layout.fragment_live_session, container, false);
        
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        // Create the observer which updates the UI.
        locationObserver = new Observer<Location>() {

            @Override
            public void onChanged(@Nullable final Location newData) {
                String loc = newData.getLatitude() + "," + newData.getLongitude();
                ((TextView) lView.findViewById(R.id.liveSession_location)).setText(loc);
                ((TextView) lView.findViewById(R.id.liveSession_speed)).setText(String.valueOf(newData.getSpeed()));
            }
        };

        final Observer<Float> distanceObserver = new Observer<Float>() {
            @Override
            public void onChanged(@Nullable final Float newData) {
                ((TextView) lView.findViewById(R.id.liveSession_distance)).setText(Float.toString(newData) + " m ");
            }
        };
        final Observer<Long> timeLeftObserver = new Observer<Long>() {
            @Override
            public void onChanged(@Nullable final Long newData) {
                // Format timeleft to proper format
                String lFormattedTime = new SimpleDateFormat("mm:ss").format(new Date(newData));
                ((TextView) lView.findViewById(R.id.liveSessionInterval_countdown)).setText(lFormattedTime);

            }
        };

        mAppViewModel.getLocation().observe(this, locationObserver);
        mAppViewModel.getDistance().observe(this, distanceObserver);
        mAppViewModel.getTimeLeft().observe(this, timeLeftObserver);

        // Get map view and set
        mMapView =  lView.findViewById(R.id.liveSession_map);
        mMapView.setTileSource(TileSourceFactory.OpenTopo);
        // Center map on location
        IMapController mapController = mMapView.getController();
        mapController.setZoom(15.0);
        // FIXME: set this to actual location
        mapController.setCenter(new GeoPoint(48.8583, 2.2944));


        // Set up button click events
        mStartbutton = lView.findViewById(R.id.liveSessionToggleBtn);
        mStartbutton.setText("Start");
        mStartbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mServiceIsRunning) startService();
                else stopService();
            }
        });

        return lView;
    }

    protected void showSessionList( )
    {
        // Begin fragment trasaction and replace content frame with session list fragment
        SessionListFragment mFrag = SessionListFragment.newInstance();
        FragmentManager mFragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();

        mFragTransaction.replace(R.id.mainContentFrame,mFrag);
        mFragTransaction.addToBackStack(null);//for back function
        mFragTransaction.commit();

    }

}
