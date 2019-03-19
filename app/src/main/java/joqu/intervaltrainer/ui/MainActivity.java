package joqu.intervaltrainer.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import joqu.intervaltrainer.R;

import static joqu.intervaltrainer.Const.TAG;


public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {
    AppViewModel mAppViewModel;
    DrawerLayout mDrawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_view);


        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        setToolbar();

        // Defines the navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
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
        checkPermissions();



    }

    @Override
    protected void onStop() {
        super.onStop();
    }




    @Override
    protected void onStart() {
        super.onStart();
    }

    // Permissions check
    protected boolean checkPermissions(){
        // Verify permissions
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                permissions[0]) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"Permissions Failure ");
            requestPermissions(permissions);
            return false;
        }else return true;
    }

    protected void requestPermissions(String[] permissions)
    {
        ActivityCompat.requestPermissions(this,permissions,0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i =0; i< grantResults.length;i++){
            Log.i(TAG,permissions[i] + ": " + grantResults[i]);
            // TODO: maybe prompt with information specifying that location/storage is 100% needed
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                continue;
            else checkPermissions();// Loop until we get what we need
        }
    }

    // Override for the Options menu item in main toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_launcher_background);
    }

    protected void showSessionList( )
    {
        // Begin fragment trasaction and replace content frame with session list fragment
        SessionListFragment mFrag = SessionListFragment.newInstance();
        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();

        mFragTransaction.replace(R.id.mainContentFrame,mFrag);
        mFragTransaction.addToBackStack(null);//for back function
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
