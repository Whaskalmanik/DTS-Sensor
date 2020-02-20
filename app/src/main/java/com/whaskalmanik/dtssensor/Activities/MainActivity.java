package com.whaskalmanik.dtssensor.Activities;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.whaskalmanik.dtssensor.FileParser;
import com.whaskalmanik.dtssensor.Fragments.AntistokesFragment;
import com.whaskalmanik.dtssensor.Fragments.StokesFragment;
import com.whaskalmanik.dtssensor.Fragments.TemperatureFragment;
import com.whaskalmanik.dtssensor.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private LineChart chart;

    double[] delka=null;
    double[] stokes=null;
    double[] antistokes=null;
    double[] teplota=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        LineChart chart;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //SeekBar seekBar = findViewById(R.id.zoomBar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        setHeader(savedInstanceState);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "max";
            String description = "maximalní hodnota";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void pop()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.password_icon)
                .setContentTitle("Varování")
                .setContentText("Teplota ve vlákně dosáhla varovné hodnoty")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    public void setHeader(Bundle savedInstanceState) {

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            TemperatureFragment tmp = TemperatureFragment.newInstance(delka, teplota);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tmp).commit();
            navigationView.setCheckedItem(R.id.tempterature);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tempterature: {
                TemperatureFragment fragment=TemperatureFragment.newInstance(delka,teplota);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            }
            case R.id.stokes: {
                StokesFragment fragment=StokesFragment.newInstance(delka,stokes);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            }
            case R.id.antistokes: {
                AntistokesFragment fragment=AntistokesFragment.newInstance(delka,antistokes);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            }
            case R.id.psChange: {
                createNotificationChannel();
                pop();
                break;
            }
            case R.id.logOut: {
                FileParser f=new FileParser(getApplication().getApplicationContext());
                f.extractFile();
                break;
            }
            case R.id.settings: {
                Intent n = new Intent(this,SettingsActivity.class);
                startActivity(n);
                break;
            }
            case R.id.refresh: {
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Refreshes the display if the network connection and the
    // pref settings allow it.

}
