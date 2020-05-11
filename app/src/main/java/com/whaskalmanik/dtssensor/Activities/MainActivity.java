package com.whaskalmanik.dtssensor.Activities;



import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.whaskalmanik.dtssensor.Fragments.MeasurementsFragment;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Files.FileParser;
import com.whaskalmanik.dtssensor.Fragments.RealTimeFragment;
import com.whaskalmanik.dtssensor.Fragments.HeatFragment;
import com.whaskalmanik.dtssensor.Fragments.TemperatureFragment;

import com.whaskalmanik.dtssensor.R;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,RealTimeFragment.FragmentRealTimeListener
{
    TemperatureFragment TemperatureFragment;
    HeatFragment HeatFragment;
    RealTimeFragment RealTimeFragment;
    MeasurementsFragment MeasurementsFragment;

    private DrawerLayout drawer;
    private FileParser fp;
    ArrayList<ExtractedFile> listOfFiles;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Preferences.initialize(getApplicationContext());
        //Toast.makeText(getApplicationContext(),Preferences.getIP(),Toast.LENGTH_SHORT).show();
        TemperatureFragment = TemperatureFragment.newInstance(0);
        HeatFragment = HeatFragment.newInstance();
        RealTimeFragment = RealTimeFragment.newInstance();
        MeasurementsFragment = MeasurementsFragment.newInstance();

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
        switch (item.getItemId())
        {
            case R.id.tempterature:
            {
                //TemperatureFragment = TemperatureFragment.newInstance(listOfFiles,xValue);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TemperatureFragment).commit();
                break;
            }
            case R.id.stokes:
            {
                //HeatFragment = HeatFragment.newInstance(listOfFiles);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HeatFragment).commit();
                break;
            }
            case R.id.realTime:
            {
                //RealTimeFragment = RealTimeFragment.newInstance(listOfFiles);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RealTimeFragment).commit();
                break;
            }
            case R.id.measurements:
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MeasurementsFragment).commit();
                break;
            }
            case R.id.settings:
            {
                Intent n = new Intent(this, SettingsActivity.class);
                startActivity(n);
                break;
            }
            case R.id.refresh:
            {
                listOfFiles=fp.extractFiles();
                break;
            }
            case R.id.storageDelete:
            {
                deleteRecursive(getApplicationContext().getFilesDir());
                Toast.makeText(getApplicationContext(),"Removing data",Toast.LENGTH_LONG).show();
                SharedPreferences pref= getApplicationContext().getSharedPreferences("SelectedPreferences",0);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("selected");
                editor.commit();
                MeasurementsFragment = MeasurementsFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MeasurementsFragment).commit();
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
    public void onValueSent(float number)
    {
        TemperatureFragment = TemperatureFragment.newInstance(number);
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TemperatureFragment).commit();
    }

}
