package joqu.intervaltrainer;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import joqu.intervaltrainer.model.IntervalData;
import joqu.intervaltrainer.model.Session;
import joqu.intervaltrainer.ui.IntervalDataAdapter;
import joqu.intervaltrainer.ui.SavedSessionViewModel;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_session_view);


        SavedSessionViewModel mSessionViewModel =  new SavedSessionViewModel(this.getApplication(),0);

        // Session information
        Session msess = mSessionViewModel.getSession();
        TextView sessionStartedText = findViewById(R.id.sessionStartedText);
        sessionStartedText.setText(msess.started);
        TextView sessionEndedText = findViewById(R.id.sessionEndedText);
        sessionEndedText.setText(msess.ended);

        // Populate recycler for IntervalData items
        RecyclerView intervalDataRecyclerView = findViewById(R.id.intervalDataView);
        final IntervalDataAdapter mAdapter = new IntervalDataAdapter(this);
        intervalDataRecyclerView.setAdapter(mAdapter);
        intervalDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //  Link Observer to ViewModel and call set method in adapter to add the data to the view
        // Set observer of LiveData sessions from viewmodel
        // When change is observed chnage the view via the adapter

        mSessionViewModel.getIntervalData().observe(this, new Observer<List<IntervalData>>() {
            @Override
            public void onChanged(@Nullable List<IntervalData> intervalData) {
                mAdapter.setIntervalData(intervalData);

            }
        });

    }
}
