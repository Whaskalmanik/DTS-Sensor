package com.whaskalmanik.dtssensor.Utils;

import android.content.Context;

import com.whaskalmanik.dtssensor.Preference.SynchronizationPref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class FileWatcher {
    private Timer refreshTimer = new Timer();
    private final SynchronizationPref synchronizationPref;
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
        synchronizationPref = new SynchronizationPref(context);

        if (synchronizationPref.isEnabled())
        {
            refreshTimer.schedule(refreshTask, 0, synchronizationPref.getFrequency() * 1000);
        }
    }

    public void disableRefresh()
    {
        if (synchronizationPref.isEnabled() || refreshTimer == null) {
            return;
        }

        refreshTimer.cancel();
        refreshTimer = null;
    }

    public void enableRefresh()
    {
        if (!synchronizationPref.isEnabled()) {
            return;
        }

        if (refreshTimer != null) {
            disableRefresh();
        }

        refreshTimer = new Timer();
        refreshTimer.schedule(refreshTask, 0, synchronizationPref.getFrequency() * 1000);
    }

    public void onRefreshFrequencyChanged()
    {
        if (!synchronizationPref.isEnabled())
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

        if (!synchronizationPref.isEnabled())
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
