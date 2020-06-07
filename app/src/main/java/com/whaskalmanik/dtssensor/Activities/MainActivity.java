package com.whaskalmanik.dtssensor.Activities;



import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.whaskalmanik.dtssensor.Database.DownloadMeasurementTask;
import com.whaskalmanik.dtssensor.Files.PeriodicTask;
import com.whaskalmanik.dtssensor.Fragments.MeasurementsFragment;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Fragments.RealTimeFragment;
import com.whaskalmanik.dtssensor.Fragments.HeatFragment;
import com.whaskalmanik.dtssensor.Fragments.TemperatureFragment;

import com.whaskalmanik.dtssensor.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,RealTimeFragment.FragmentRealTimeListener
{
    private static Class fragmentType;

    private TemperatureFragment TemperatureFragment;
    private HeatFragment HeatFragment;
    private RealTimeFragment RealTimeFragment;
    private MeasurementsFragment MeasurementsFragment;
    private PeriodicTask refreshTask;
    private DrawerLayout drawer;
    private DownloadMeasurementTask downloadMeasurementTask;
    private Map<Class, Fragment> fragments = new HashMap<>();
    private NavigationView navigationView;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Preferences.initialize(getApplicationContext());
        Preferences.listenOnSelectedChanged(this::checkForEnabledHeader);

        initializeFragments();
        initializePeriodicTask();
        createDrawer(savedInstanceState);
        if(Preferences.isFirstStart())
        {
            firstTimeSetup();
        }
    }

    private void initializePeriodicTask()
    {
        refreshTask = new PeriodicTask();
        if(!Preferences.isSynchronizationEnabled())
        {
            refreshTask.disableRefresh();
        }
        refreshTask.setAction(this::refreshData);
    }

    private void initializeFragments()
    {
        TemperatureFragment = TemperatureFragment.newInstance(Float.MIN_VALUE);
        HeatFragment = HeatFragment.newInstance();
        RealTimeFragment = RealTimeFragment.newInstance(Integer.MIN_VALUE);
        MeasurementsFragment = MeasurementsFragment.newInstance();

        fragments.put(TemperatureFragment.getClass(), TemperatureFragment);
        fragments.put(HeatFragment.getClass(), HeatFragment);
        fragments.put(RealTimeFragment.getClass(), RealTimeFragment);
        fragments.put(MeasurementsFragment.getClass(), MeasurementsFragment);
    }

    private void firstTimeSetup()
    {
        runOptionsActivity();
        Preferences.setFirstStart(false);
        checkForEnabledHeader(null);
    }

    private void createDrawer(Bundle savedInstanceState)
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        setHeader(savedInstanceState);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
    }

    private void checkForEnabledHeader(String selectedValue)
    {
        if(selectedValue==null)
        {
            navigationView.getMenu().findItem(R.id.realTime).setEnabled(false);
            navigationView.getMenu().findItem(R.id.tempterature).setEnabled(false);
            navigationView.getMenu().findItem(R.id.heat).setEnabled(false);
        }
        else
        {
            navigationView.getMenu().findItem(R.id.realTime).setEnabled(true);
            navigationView.getMenu().findItem(R.id.tempterature).setEnabled(true);
            navigationView.getMenu().findItem(R.id.heat).setEnabled(true);
        }
    }

    public void setHeader(Bundle savedInstanceState)
    {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            fragmentType = MeasurementsFragment.getClass();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MeasurementsFragment).commit();
            navigationView.setCheckedItem(R.id.measurements);
        }
    }
    private void reloadFragment()
    {
        if (fragmentType == null) {
            return;
        }
        if(downloadMeasurementTask != null && fragmentType.equals(RealTimeFragment.getClass()))
        {
            RealTimeFragment = RealTimeFragment.newInstance(downloadMeasurementTask.getLastIndex());
            fragments.put(RealTimeFragment.getClass(), RealTimeFragment);
        }
        Log.d("MainActivity","Fragment refreshed");
        Fragment selectedFragment = fragments.get(fragmentType);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(selectedFragment);
        ft.attach(selectedFragment);
        ft.replace(R.id.fragment_container, selectedFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        if (Preferences.isSynchronizationEnabled()) {
            refreshTask.enableRefresh();
        }
        switch (item.getItemId())
        {
            case R.id.tempterature:
            {
                fragmentType = TemperatureFragment.getClass();
                reloadFragment();
                break;
            }
            case R.id.heat:
            {
                fragmentType = HeatFragment.getClass();
                reloadFragment();
                break;
            }
            case R.id.realTime:
            {
                fragmentType = RealTimeFragment.getClass();
                reloadFragment();
                break;
            }
            case R.id.measurements:
            {
                fragmentType = MeasurementsFragment.getClass();
                reloadFragment();
                refreshTask.disableRefresh();
                break;
            }
            case R.id.settings:
            {
                runOptionsActivity();
                break;
            }
            case R.id.refresh:
            {
                refreshTask.manualRefresh();
                reloadFragment();
                break;
            }
            case R.id.storageDelete:
            {
                fragmentType=MeasurementsFragment.getClass();
                unselect();
                reloadFragment();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(Preferences.isSynchronizationEnabled())
        {
            refreshTask.onRefreshFrequencyChanged();
        }
        reloadFragment();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        refreshTask.disableRefresh();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (downloadMeasurementTask != null) {
            downloadMeasurementTask.setCallback(null);
        }
        Preferences.listenOnSelectedChanged(null);
    }

    private void unselect()
    {
        Preferences.setSelectedValue(null);
        deleteRecursive(getApplicationContext().getFilesDir());
    }


    private void refreshData() {
        downloadMeasurementTask = new DownloadMeasurementTask(getApplicationContext(),Preferences.getSelectedValue(),false);
        downloadMeasurementTask.setCallback(this::reloadFragment);
        downloadMeasurementTask.execute();
    }

    @Override
    public void onValueSent(float number) {
        TemperatureFragment = TemperatureFragment.newInstance(number);
        fragments.put(TemperatureFragment.getClass(), TemperatureFragment);
    }

    private void runOptionsActivity()
    {
        refreshTask.disableRefresh();
        Intent n = new Intent(this, SettingsActivity.class);
        startActivity(n);
    }

}
