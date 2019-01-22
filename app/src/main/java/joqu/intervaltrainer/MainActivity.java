package joqu.intervaltrainer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.sessionRecyclerView);
        final SessionListAdapter adapter = new SessionListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //  TODO: Link Observer to ViewModel and call set method in adapter to add the data to the view
        // Create Session View Model var
        SessionViewModel mSessionViewModel;
        // Get model via ViewModelProviders
        mSessionViewModel =  new SessionViewModel(this.getApplication(),0);
       // ViewModelProviders.of(this).get(SessionViewModel.class);

        // Set observer of LiveData sessions from viewmodel
        // When change is observed chnage the view via the adapter
        mSessionViewModel.getSessions().observe(this, new Observer<List<Session>>() {
            @Override
            public void onChanged(@Nullable List<Session> sessions) {
                adapter.setSessions(sessions);

            }
        });

        mSessionViewModel.getIntervalData().observe(this, new Observer<List<IntervalData>>() {
            @Override
            public void onChanged(@Nullable List<IntervalData> intervalData) {
                adapter.setIntervalData(intervalData);

            }
        });


    }
}
