package com.whaskalmanik.dtssensor.files;

import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.utils.Command;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ForkJoinPool;

public class PeriodicTask {
    private Timer refreshTimer = new Timer();
    private Boolean isRefreshing = false;
    private final Object refreshLock = new Object();
    private Command action;

    public PeriodicTask() {
        Preferences.areMarkersEnabled();

        if (Preferences.isSynchronizationEnabled()) {
            refreshTimer.schedule(getRefreshTask(), 0, Preferences.getFrequency() * 1000);
        }
    }

    private TimerTask getRefreshTask() {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                synchronized (refreshLock)
                {
                    if (isRefreshing) {
                        return;
                    }
                    isRefreshing = true;
                }

                if (action != null) {
                    action.apply();
                }

                isRefreshing = false;
            }
        };
    }

    public void disableRefresh() {
        if (refreshTimer == null) {
            return;
        }

        refreshTimer.cancel();
        refreshTimer = null;
    }

    public void enableRefresh() {
        if (refreshTimer != null) {
            return;
        }

        refreshTimer = new Timer();
        refreshTimer.schedule(getRefreshTask(), 0, Preferences.getFrequency() * 1000);
    }

    public void onRefreshFrequencyChanged() {
        disableRefresh();
        enableRefresh();
    }

    public void manualRefresh()
    {
        ForkJoinPool.commonPool().execute(() -> getRefreshTask().run());
    }

    public void setAction(Command action)
    {
        this.action = action;
    }
}
