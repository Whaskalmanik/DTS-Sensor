package com.whaskalmanik.dtssensor.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.whaskalmanik.dtssensor.database.DownloadMeasurementTask;
import com.whaskalmanik.dtssensor.files.PeriodicTask;
import com.whaskalmanik.dtssensor.fragments.MeasurementsFragment;
import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.fragments.RealTimeFragment;
import com.whaskalmanik.dtssensor.fragments.HeatFragment;
import com.whaskalmanik.dtssensor.fragments.TemperatureFragment;

import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,RealTimeFragment.FragmentRealTimeListener
{
    private static Class fragmentType;
    private static float tempNumber= Float.MIN_VALUE;
    private TemperatureFragment temperatureFragment;
    private HeatFragment heatFragment;
    private RealTimeFragment realTimeFragment;
    private MeasurementsFragment measurementsFragment;
    private Map<Class, Fragment> fragments = new HashMap<>();

    private PeriodicTask refreshTask;
    private DownloadMeasurementTask downloadMeasurementTask;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Preferences.listenOnSelectedChanged(this::checkForEnabledHeader);

        initializeFragments();
        initializePeriodicTask();
        createDrawer(savedInstanceState);
        if(Preferences.isFirstStart()) {
            firstTimeSetup();
        }
        checkForEnabledHeader(Preferences.getSelectedValue());
    }

    private void initializePeriodicTask() {
        refreshTask = new PeriodicTask();
        if(!Preferences.isSynchronizationEnabled()) {
            refreshTask.disableRefresh();
        }
        refreshTask.setAction(this::refreshData);
    }

    private void initializeFragments() {
        temperatureFragment = createFragment(() -> TemperatureFragment.newInstance(tempNumber));
        heatFragment = createFragment(HeatFragment::newInstance);
        realTimeFragment = createFragment(() -> RealTimeFragment.newInstance(Integer.MIN_VALUE));
        measurementsFragment = createFragment(MeasurementsFragment::newInstance);
    }

    private <T extends Fragment> T createFragment(Supplier<T> factory) {
        T fragment = factory.get();
        fragments.put(fragment.getClass(), fragment);
        return fragment;
    }

    private void firstTimeSetup() {
        runOptionsActivity();
        Preferences.setFirstStart(false);
        checkForEnabledHeader(null);
    }

    private void createDrawer(Bundle savedInstanceState) {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Measurements");
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        setHeader(savedInstanceState);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
    }

    private void checkForEnabledHeader(String selectedValue) {
        boolean enabled = selectedValue != null;
        navigationView.getMenu().findItem(R.id.realTime).setEnabled(enabled);
        navigationView.getMenu().findItem(R.id.temperature).setEnabled(enabled);
        navigationView.getMenu().findItem(R.id.heat).setEnabled(enabled);
    }

    public void setHeader(Bundle savedInstanceState) {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            return;
        }
        fragmentType = measurementsFragment.getClass();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, measurementsFragment).commit();
        navigationView.setCheckedItem(R.id.measurements);
    }
    public void setTitle()
    {
        if(fragmentType==measurementsFragment.getClass())
        {
            toolbar.setTitle("Measurements");
        }
        else if(fragmentType==realTimeFragment.getClass())
        {
            toolbar.setTitle("Real time graph");
        }
        else if(fragmentType==temperatureFragment.getClass())
        {
            toolbar.setTitle("Temperature graph");
        }
        else if(fragmentType==heatFragment.getClass())
        {
            toolbar.setTitle("Heat graph");
        }
    }
    private void reloadFragment() {
        if (fragmentType == null) {
            return;
        }
        if(downloadMeasurementTask != null && fragmentType.equals(realTimeFragment.getClass())) {
            realTimeFragment = createFragment(() -> RealTimeFragment.newInstance(DownloadMeasurementTask.getLastIndex()));
        }
        setTitle();
        Log.d("MainActivity","Fragment refreshed");
        Fragment selectedFragment = fragments.get(fragmentType);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(selectedFragment);
        ft.attach(selectedFragment);
        ft.replace(R.id.fragment_container, selectedFragment);
        ft.commitAllowingStateLoss();
    }

    private void refreshData() {
        downloadMeasurementTask = new DownloadMeasurementTask(getApplicationContext(),Preferences.getSelectedValue(),false);
        downloadMeasurementTask.setCallback(this::reloadFragment);
        downloadMeasurementTask.execute();
    }

    private void runOptionsActivity() {
        refreshTask.disableRefresh();
        Intent n = new Intent(this, SettingsActivity.class);
        startActivityForResult(n,0);
    }

    private AlertDialog askOption() {
        return new AlertDialog.Builder(this)
                .setTitle("Delete data")
                .setMessage("Do you really want to delete all files downloaded from database?")
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setPositiveButton(Html.fromHtml("<font color='#b31e0b'>Delete</font>"), (dialog, whichButton) -> {
                    fragmentType= measurementsFragment.getClass();
                    Preferences.setSelectedValue(null);
                    if(Utils.deleteRecursive(getApplicationContext().getFilesDir()))
                    {
                        Toast.makeText(getApplicationContext(), "Data was deleted!", Toast.LENGTH_SHORT).show();
                    }
                    reloadFragment();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("result",0);
                switch (result)
                {
                    case 1:
                    {
                        fragmentType = measurementsFragment.getClass();
                        reloadFragment();
                        break;
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (Preferences.isSynchronizationEnabled()) {
            refreshTask.enableRefresh();
        }
        switch (item.getItemId()) {
            case R.id.temperature: {
                fragmentType = temperatureFragment.getClass();
                reloadFragment();
                break;
            }
            case R.id.heat: {
                fragmentType = heatFragment.getClass();
                reloadFragment();
                break;
            }
            case R.id.realTime: {
                fragmentType = realTimeFragment.getClass();
                reloadFragment();
                break;
            }
            case R.id.measurements: {
                fragmentType = measurementsFragment.getClass();
                //refreshTask.disableRefresh();
                reloadFragment();
                break;
            }
            case R.id.settings: {
                runOptionsActivity();
                break;
            }
            case R.id.refresh: {
                refreshTask.manualRefresh();
                reloadFragment();
                break;
            }
            case R.id.storageDelete: {
                AlertDialog dialog = askOption();
                dialog.show();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(Preferences.isSynchronizationEnabled()) {
            refreshTask.onRefreshFrequencyChanged();
        }
        reloadFragment();
    }
    @Override
    public void onPause() {
        super.onPause();
        refreshTask.disableRefresh();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadMeasurementTask != null) {
            downloadMeasurementTask.setCallback(null);
        }
        Preferences.listenOnSelectedChanged(null);
    }

    @Override
    public void onValueSent(float number) {
        temperatureFragment = createFragment(() -> TemperatureFragment.newInstance(number));
        tempNumber=number;
    }

}
