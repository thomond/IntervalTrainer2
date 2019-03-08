package joqu.intervaltrainer.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import joqu.intervaltrainer.LiveSessionService;
import joqu.intervaltrainer.R;
import joqu.intervaltrainer.model.LiveSession;


public class MainActivity extends AppCompatActivity {
    AppViewModel mSessionViewModel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_view);


        mSessionViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

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
                        if (menuItem.getItemId() == R.id.nav_item_saved_session  )
                            showSessionList();
                        else if (menuItem.getItemId() == R.id.nav_item_saved_template  )
                            showTemplateList();
                        else if (menuItem.getItemId() == R.id.nav_item_new_session  )
                            showLiveSession();
                        //else if (menuItem.getItemId() == R.id.nav_item_new_session  )
                        //    return true;
                        return true;
                    }
                });




    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_launcher_background);
    }

    protected void showSessionList( )
    {
        // Begin fragment trasaction and replace content frame with session list fragment
        SessionListFragment mFrag = SessionListFragment.newInstance();
        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();

        mFragTransaction.replace(R.id.mainContentFrame,mFrag);

        mFragTransaction.commit();

    }

    protected void showTemplateList()
    {
        // Begin fragment trasaction and replace content frame with session list fragment
       TemplateFragment mFrag = TemplateFragment.newInstance();
        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();

        mFragTransaction.replace(R.id.mainContentFrame,mFrag);

        mFragTransaction.commit();
    }

    protected void showLiveSession()
    {
        // Begin fragment trasaction and replace content frame with session  fragment
        LiveSessionFragment mFrag = LiveSessionFragment.newInstance();
        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();

        mFragTransaction.replace(R.id.mainContentFrame,mFrag);

        mFragTransaction.commit();
    }
}
