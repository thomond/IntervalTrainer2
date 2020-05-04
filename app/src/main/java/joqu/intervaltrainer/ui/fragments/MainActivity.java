package joqu.intervaltrainer.ui.fragments;

import android.Manifest;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import joqu.intervaltrainer.Const;
import joqu.intervaltrainer.R;
import joqu.intervaltrainer.ui.AppViewModel;

import static joqu.intervaltrainer.Const.TAG;


public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {
    AppViewModel mAppViewModel;
    DrawerLayout mDrawerLayout;
    private int mSavedTemplateId;
    public static boolean isVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.default_view);

        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        setToolbar();

        checkPermissions();
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(Const.APP_NAME,0);

        if(prefs.contains("template_id")){
            mSavedTemplateId = prefs.getInt("template_id",-1);
        }else mSavedTemplateId = -1;

        // Switch to main screen
        switchFragment(MainFragment.newInstance(), R.id.mainContentFrame,getSupportFragmentManager());

    }


    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume(); isVisible = true;
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
                switchFragment(MainFragment.newInstance(), R.id.mainContentFrame,getSupportFragmentManager());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switchFragment(MainFragment.newInstance(), R.id.mainContentFrame,getSupportFragmentManager());

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        //actionbar.setDisplayHomeAsUpEnabled (true);
        actionbar.setHomeButtonEnabled(true);

        //actionbar.setHomeAsUpIndicator(R.drawable.moreinfo_arrow);
    }

    public static void switchFragment(Fragment f, int rid,FragmentManager fragmentManager)
    {
        switchFragment(f,rid,fragmentManager,null);

    }

    public static void switchFragment(Fragment f, int rid,FragmentManager fragmentManager,  Bundle fragmentBundle) {
        // Begin fragment trasaction and replace content frame with session list fragment
        FragmentTransaction mFragTransaction = fragmentManager.beginTransaction();
        if(fragmentBundle!=null) f.setArguments(fragmentBundle);
        mFragTransaction.replace(rid,f);
        mFragTransaction.commit();
    }


    protected void showSessionList( )
    {
        // Begin fragment trasaction and replace content frame with session list fragment
        SavedSessionFragment mFrag = SavedSessionFragment.newInstance();
        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();

        mFragTransaction.replace(R.id.mainContentFrame,mFrag);
        mFragTransaction.addToBackStack(mFrag.getClass().getSimpleName());//for back function
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
        // Include template id in argument list
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt("template_id",mSavedTemplateId);
        mFrag.setArguments(fragmentBundle);

        FragmentManager mFragManager = getSupportFragmentManager();
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();

        mFragTransaction.replace(R.id.mainContentFrame,mFrag);

        mFragTransaction.commit();
    }
}
