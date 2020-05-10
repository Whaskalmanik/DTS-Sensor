package com.whaskalmanik.dtssensor.Files;

import android.content.Context;

import com.whaskalmanik.dtssensor.Preferences.Preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class FileWatcher {
    private Timer refreshTimer = new Timer();
    private Boolean isRefreshing;
    private Context context;
    private Consumer<Collection<String>> filesFoundAction;

    private final TimerTask refreshTask = new TimerTask()
    {

        @Override
        public void run()
        {
            synchronized (isRefreshing)
            {
                if (isRefreshing) {
                    return;
                }
                isRefreshing = true;
            }

            //TODO
            //To, co se bude pravidelně spouštět
            List<String> newPaths = new ArrayList<>();

            if (filesFoundAction != null) {
                filesFoundAction.accept(newPaths);
            }

            isRefreshing = false;
        }
    };

    public FileWatcher(Context context)
    {
        this.context = context;
        Preferences.areMarkersEnabled();

        if (Preferences.isSynchronizationEnabled())
        {
            refreshTimer.schedule(refreshTask, 0, Preferences.getFrequency() * 1000);
        }
    }

    public void disableRefresh()
    {
        if (Preferences.isSynchronizationEnabled() || refreshTimer == null) {
            return;
        }

        refreshTimer.cancel();
        refreshTimer = null;
    }

    public void enableRefresh()
    {
        if (!Preferences.isSynchronizationEnabled() ) {
            return;
        }

        if (refreshTimer != null) {
            disableRefresh();
        }

        refreshTimer = new Timer();
        refreshTimer.schedule(refreshTask, 0, Preferences.getFrequency() * 1000);
    }

    public void onRefreshFrequencyChanged()
    {
        if (!Preferences.isSynchronizationEnabled())
        {
            return;
        }

        disableRefresh();
        enableRefresh();
    }

    public void manualRefresh()
    {
        if (refreshTimer == null)
        {
            refreshTimer = new Timer();
        }

        refreshTimer.schedule(refreshTask, 0);

        if (!Preferences.isSynchronizationEnabled())
        {
            disableRefresh();
        }
    }

    /**
     *
     * @param action A functoid that accepts a collection of new file paths
     */
    public void setFilesFoundAction(Consumer<Collection<String>> action)
    {
        filesFoundAction = action;
    }
}
