package joqu.intervaltrainer.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.model.Session;
import joqu.intervaltrainer.ui.IntervalDataAdapter;
import joqu.intervaltrainer.ui.ItemClickListener;
import joqu.intervaltrainer.ui.SavedSessionViewModel;
import joqu.intervaltrainer.ui.SessionListAdapter;
import joqu.intervaltrainer.ui.TemplateListAdapter;


public class MainActivity extends AppCompatActivity {
    SavedSessionViewModel mSessionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_view);


        mSessionViewModel = ViewModelProviders.of(this).get(SavedSessionViewModel.class);

        setToolbar();

        final DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        if (menuItem.getItemId() == R.id.nav_item_new_session  )
                            return true;
                        else if (menuItem.getItemId() == R.id.nav_item_saved_session  )
                            showSessionList(mSessionViewModel);
                        else if (menuItem.getItemId() == R.id.nav_item_saved_template  )
                            showTemplateList(mSessionViewModel);
                        //else if (menuItem.getItemId() == R.id.nav_item_new_session  )
                        //    return true;
                        return true;
                    }
                });






    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_launcher_background);
    }

    protected void showSession(SavedSessionViewModel mViewModel, int sessionID ){
        setContentView(R.layout.saved_session_view);
        setToolbar();
        // Session information
        Session msess = mSessionViewModel.getSessionById(sessionID);
        TextView sessionStartedText = findViewById(R.id.sessionItem_started);
        sessionStartedText.setText(msess.started);
        TextView sessionEndedText = findViewById(R.id.sessionItem_ended);
        sessionEndedText.setText(msess.ended);

        // Populate recycler for IntervalData items
        RecyclerView intervalDataRecyclerView = findViewById(R.id.intervalDataView);
        final IntervalDataAdapter mAdapter = new IntervalDataAdapter(this);

        intervalDataRecyclerView.setAdapter(mAdapter);
        intervalDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //  Link Observer to ViewModel and call set method in adapter to add the data to the view
        // Set observer of LiveData sessions from viewmodel
        // When change is observed chnage the view via the adapter

        mAdapter.setIntervalData(mSessionViewModel.getSessionIntervalData(sessionID));
    }

    protected void showSessionList(final SavedSessionViewModel mViewModel )
    {
        setContentView(R.layout.session_list_view);
        setToolbar();
        // Populate recycler for  items
        RecyclerView sessionDataRecyclerView = findViewById(R.id.sessionListRecyclerView);

        final SessionListAdapter mAdapter = new SessionListAdapter(this, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showSession(mViewModel,position);
            }

            @Override
            public void onItemClick(View view, int position, int id) {
                showSession(mViewModel,id);
            }
        });

        sessionDataRecyclerView.setAdapter(mAdapter);
        sessionDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setSessionList(mViewModel.getAllSessions(),mViewModel.getAllTemplates());

    }

    protected void showTemplateList(SavedSessionViewModel mViewModel )
    {
        setContentView(R.layout.template_list_view);
        setToolbar();
        // Populate recycler for  items
        RecyclerView templateDataRecyclerView = findViewById(R.id.templateListRecyclerView);
        final TemplateListAdapter mAdapter = new TemplateListAdapter(this);
        templateDataRecyclerView.setAdapter(mAdapter);
        templateDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setTemplateList(mViewModel.getAllTemplates());

    }
}
