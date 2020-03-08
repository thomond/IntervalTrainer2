package joqu.intervaltrainer.ui.fragments;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.dynamic.SupportFragmentWrapper;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.Objects;

import joqu.intervaltrainer.BuildConfig;
import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.services.LiveSessionService;
import joqu.intervaltrainer.R;
import joqu.intervaltrainer.ui.AppViewModel;
import joqu.intervaltrainer.ui.LiveSessionObserver;

import static androidx.appcompat.app.AlertDialog.*;
import static joqu.intervaltrainer.Const.BROADCAST_SVC_STARTED;
import static joqu.intervaltrainer.Const.BROADCAST_SVC_STOPPED;
import static joqu.intervaltrainer.ui.fragments.MainActivity.switchFragment;

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
                case BROADCAST_SVC_STOPPED: {
                    mServiceIsRunning = false;
                    if (mStartbutton != null) mStartbutton.setText("Start");
                    //Check if backgrounded
                    if (MainActivity.isVisible) {
                        // Try to aquire frgment manager from either activity or context
                        FragmentManager fm;
                        fm = getActivity().getSupportFragmentManager();
                        if(fm==null) fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
                        if(fm==null) throw new IllegalStateException();

                        switchFragment(SavedSessionFragment.newInstance(), R.id.mainContentFrame, fm);
                        break;
                    }
                }
                case BROADCAST_SVC_STARTED:
                {
                    mServiceIsRunning = true;
                    if (mStartbutton!=null) mStartbutton.setText("Stop");
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + action);
            }

        }
    };

    AppViewModel mAppViewModel;

    private boolean mServiceIsRunning = false;
    private Button mStartbutton;
    private MapView mMapView;
    private LiveSessionObserver mLiveSessionObserver;
    private int mSavedTemplateId;



    public LiveSessionFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided time.
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

        Bundle arguments = getArguments();
        if(arguments!=null) {
            if(arguments.containsKey("template_id"))
                mSavedTemplateId = arguments.getInt("template_id",-1);
            else mSavedTemplateId = -1;
        }

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

        if (mSavedTemplateId == -1) {
            if(BuildConfig.DEBUG)mSavedTemplateId = 1;
            else{
                Log.e(Const.TAG, "Template ID not valid. " + mSavedTemplateId);
                throw new UnsupportedOperationException("Template ID not valid. ");
            }
        }

        Intent intent = new Intent(getContext(), LiveSessionService.class);
        // Fill the intent with the temp id and start the service
        intent.putExtra(Const.INTENT_EXTRA_TEMPLATE_ID_INT,mSavedTemplateId);
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

        // create new session observer and link with view model
        mLiveSessionObserver = new LiveSessionObserver(lView);
        mAppViewModel.getLiveSession().observe(this, mLiveSessionObserver);

        // Get map view and set
        mMapView =  lView.findViewById(R.id.liveSession_map);
        mMapView.setTileSource(TileSourceFactory.OpenTopo);
        // Center map on location
        IMapController mapController = mMapView.getController();
        mapController.setZoom(18.0);
        // FIXME: set this to actual location
        mapController.setCenter(new GeoPoint(48.8583, 2.2944));


        // Set up button click events
        mStartbutton = lView.findViewById(R.id.liveSessionToggleBtn);
        mStartbutton.setText("Start");
        // confirm stop
        mStartbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mServiceIsRunning) startService();
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.LiveSessionConfirm);
                    builder.setPositiveButton(R.string.ok, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mServiceIsRunning) stopService();
                                    // go back to main screen
                                    MainActivity.switchFragment(MainFragment.newInstance(),R.id.mainContentFrame,getActivity().getSupportFragmentManager());
                                }
                            }
                    );
                    builder.setNegativeButton(R.string.cancel, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // NOOP
                                }
                            }
                    );

                    builder.create().show();
                }
            }
        });



        return lView;
    }

    protected void showSessionList( )
    {

        // Begin fragment trasaction and replace content frame with session list fragment
        SavedSessionFragment mFrag = SavedSessionFragment.newInstance();
        FragmentManager mFragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();

        mFragTransaction.replace(R.id.mainContentFrame,mFrag);
        mFragTransaction.addToBackStack(null);//for back function
        mFragTransaction.commit();

    }

}
