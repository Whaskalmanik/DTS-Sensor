package com.whaskalmanik.dtssensor.Activities;



import android.content.SharedPreferences;
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
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.whaskalmanik.dtssensor.Database.DownloadMeasurementTask;
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.Files.PeriodicTask;
import com.whaskalmanik.dtssensor.Fragments.MeasurementsFragment;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Fragments.RealTimeFragment;
import com.whaskalmanik.dtssensor.Fragments.HeatFragment;
import com.whaskalmanik.dtssensor.Fragments.TemperatureFragment;

import com.whaskalmanik.dtssensor.R;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,RealTimeFragment.FragmentRealTimeListener
{
    private TemperatureFragment TemperatureFragment;
    private HeatFragment HeatFragment;
    private RealTimeFragment RealTimeFragment;
    private MeasurementsFragment MeasurementsFragment;
    private PeriodicTask refreshTask;
    private SharedPreferences pref;
    private DrawerLayout drawer;
    private String selected;
    private DownloadMeasurementTask downloadMeasurementTask;
    private Map<Class, Fragment> fragments = new HashMap<>();

    private static Class fragmentType;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Preferences.initialize(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("SelectedPreferences", 0);
        selected = pref.getString("selected",null);

        TemperatureFragment = TemperatureFragment.newInstance(Float.MIN_VALUE);
        HeatFragment = HeatFragment.newInstance();
        RealTimeFragment = RealTimeFragment.newInstance(Integer.MIN_VALUE);
        MeasurementsFragment = MeasurementsFragment.newInstance();

        fragments.put(TemperatureFragment.getClass(), TemperatureFragment);
        fragments.put(HeatFragment.getClass(), HeatFragment);
        fragments.put(RealTimeFragment.getClass(), RealTimeFragment);
        fragments.put(MeasurementsFragment.getClass(), MeasurementsFragment);

        downloadMeasurementTask = new DownloadMeasurementTask(getApplicationContext(),selected,false);
        refreshTask = new PeriodicTask(getApplicationContext());
        if(!Preferences.isSynchronizationEnabled())
        {
            refreshTask.disableRefresh();
        }
        refreshTask.setAction(this::refreshData);

        setContentView(R.layout.activity_main);
        createDrawer(savedInstanceState);
    }

    private void createDrawer(Bundle savedInstanceState)
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        setHeader(savedInstanceState);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }



    public void setHeader(Bundle savedInstanceState)
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
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
        Fragment selectedFragment = fragments.get(fragmentType);
        Toast.makeText(getApplicationContext(), "BLICK FRAGMENT", Toast.LENGTH_SHORT).show();
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
                refreshTask.disableRefresh();
                Intent n = new Intent(this, SettingsActivity.class);
                startActivity(n);
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
                deleteRecursive(getApplicationContext().getFilesDir());
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
        refreshTask.disableRefresh();
        downloadMeasurementTask.setCallback(null);
    }


    void refreshData() {
        selected = pref.getString("selected",null);
        downloadMeasurementTask = new DownloadMeasurementTask(getApplicationContext(),selected,false);
        downloadMeasurementTask.setCallback(() -> {
            reloadFragment();
           // Toast.makeText(getApplicationContext(), "BLICK", Toast.LENGTH_SHORT).show();
        });
        downloadMeasurementTask.execute();
    }

    @Override
    public void onValueSent(float number) {
        TemperatureFragment = TemperatureFragment.newInstance(number);
        fragments.put(TemperatureFragment.getClass(), TemperatureFragment);
    }

}
