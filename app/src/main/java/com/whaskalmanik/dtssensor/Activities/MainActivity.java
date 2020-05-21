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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.whaskalmanik.dtssensor.Database.DownloadMeasurementTask;
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.Files.PeriodicTask;
import com.whaskalmanik.dtssensor.Fragments.MeasurementsFragment;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Files.FileParser;
import com.whaskalmanik.dtssensor.Fragments.RealTimeFragment;
import com.whaskalmanik.dtssensor.Fragments.HeatFragment;
import com.whaskalmanik.dtssensor.Fragments.TemperatureFragment;

import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Utils.Command;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,RealTimeFragment.FragmentRealTimeListener
{
    private TemperatureFragment TemperatureFragment;
    private HeatFragment HeatFragment;
    private RealTimeFragment RealTimeFragment;
    private MeasurementsFragment MeasurementsFragment;
    private DocumentsLoader documentsLoader;
    private PeriodicTask refreshTask;
    private SharedPreferences pref;
    private DrawerLayout drawer;
    private String selected;
    private DownloadMeasurementTask downloadMeasurementTask;
    private Fragment selectedFragment;

    ArrayList<ExtractedFile> listOfFiles;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Preferences.initialize(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("SelectedPreferences", 0);
        selected = pref.getString("selected",null);


        //Toast.makeText(getApplicationContext(),Preferences.getIP(),Toast.LENGTH_SHORT).show();
        TemperatureFragment = TemperatureFragment.newInstance(listOfFiles,Float.MIN_VALUE);
        HeatFragment = HeatFragment.newInstance(listOfFiles);
        RealTimeFragment = RealTimeFragment.newInstance(listOfFiles);
        MeasurementsFragment = MeasurementsFragment.newInstance();

        downloadMeasurementTask = new DownloadMeasurementTask(getApplicationContext(),selected,false);
        refreshTask = new PeriodicTask(getApplicationContext());
        //refreshTask.disableRefresh();
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
            selectedFragment=MeasurementsFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MeasurementsFragment).commit();
            navigationView.setCheckedItem(R.id.measurements);
        }
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
        refreshTask.enableRefresh();
        switch (item.getItemId())
        {
            case R.id.tempterature:
            {
                selectedFragment=TemperatureFragment;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TemperatureFragment).commit();
                break;
            }
            case R.id.stokes:
            {
                selectedFragment=HeatFragment;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HeatFragment).commit();
                break;
            }
            case R.id.realTime:
            {
                selectedFragment=RealTimeFragment;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RealTimeFragment).commit();
                break;
            }
            case R.id.measurements:
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MeasurementsFragment).commit();
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

    }

    @Override
    public void onValueSent(float number)
    {
        TemperatureFragment = TemperatureFragment.newInstance(listOfFiles,number);
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TemperatureFragment).commit();
    }

    void refreshData() {
        selected = pref.getString("selected",null);
        downloadMeasurementTask = new DownloadMeasurementTask(getApplicationContext(),selected,false);

        downloadMeasurementTask.setCallback(() -> {
            FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
            ft.detach(selectedFragment);
            ft.attach(selectedFragment);
            ft.commit();
        });
        downloadMeasurementTask.execute();
    }

}
