package joqu.intervaltrainer.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import joqu.intervaltrainer.BuildConfig;
import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.LiveSessionService;
import joqu.intervaltrainer.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveSessionFragment extends Fragment {

    LiveSessionService mService; // The service used for new session
    BroadcastReceiver mReceiver;
    AppViewModel mAppViewModel;

    private int mTemplateId;
    private boolean mServiceIsRunning = false;

    public LiveSessionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LiveSessionFragment.
     */
    public static LiveSessionFragment newInstance() {
        return new LiveSessionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    // Stop any session currently running
    public void stopService(){
        Log.i(Const.TAG,"Sending signal to stop service...");
        getContext().stopService( new Intent(getContext(), LiveSessionService.class));
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
        final View lView =  inflater.inflate(R.layout.fragment_live_session, container, false);;
        
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        // Create the observer which updates the UI.
        final Observer<Double> latObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable final Double newData) {
                ((TextView) lView.findViewById(R.id.liveSession_latitude)).setText(Double.toString(newData));
            }
        };
        final Observer<Double> longObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable final Double newData) {
                ((TextView) lView.findViewById(R.id.liveSession_longitude)).setText(Double.toString(newData));
            }
        };
        final Observer<Long> timeLeftObserver = new Observer<Long>() {
            @Override
            public void onChanged(@Nullable final Long newData) {
                ((TextView) lView.findViewById(R.id.liveSessionInterval_type)).setText(Double.toString(newData));

            }
        };

        mAppViewModel.getLiveLatitude().observe(this, latObserver);
        mAppViewModel.getLiveLongitude().observe(this, longObserver);
        mAppViewModel.getTimeLeft().observe(this, timeLeftObserver);

        // Set up button click events
        final Button button = lView.findViewById(R.id.liveSessionToggleBtn);
        button.setText("Start");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mServiceIsRunning){
                    startService();
                    mServiceIsRunning = true;
                    button.setText("Stop");
                }
                else {
                    stopService();
                    mServiceIsRunning = false;
                    button.setText("Start");
                }
            }
        });

        return lView;
    }



}
