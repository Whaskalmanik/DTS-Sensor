package com.whaskalmanik.dtssensor.Files;

import android.content.Context;

import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Utils.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public class PeriodicTask {
    private Timer refreshTimer = new Timer();
    private Boolean isRefreshing = false;
    private Context context;
    private Command action;

    public PeriodicTask(Context context)
    {
        this.context = context;
        Preferences.areMarkersEnabled();

        if (Preferences.isSynchronizationEnabled())
        {
            refreshTimer.schedule(getRefreshTask(), 0, Preferences.getFrequency() * 1000);
        }
    }

    private TimerTask getRefreshTask() {
        return new TimerTask()
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

                if (action != null) {
                    action.apply();
                }

                isRefreshing = false;
            }
        };
    }

    public void disableRefresh()
    {
        if (refreshTimer == null) {
            return;
        }

        refreshTimer.cancel();
        refreshTimer = null;
    }

    public void enableRefresh()
    {
        if (refreshTimer != null) {
            return;
        }

        refreshTimer = new Timer();
        refreshTimer.schedule(getRefreshTask(), 0, Preferences.getFrequency() * 1000);
    }

    public void onRefreshFrequencyChanged()
    {
        disableRefresh();
        enableRefresh();
    }

    public void manualRefresh()
    {
        ForkJoinPool.commonPool().execute(() -> getRefreshTask().run());
    }

    /**
     *
     * @param action A functoid that accepts a collection of new file paths
     */
    public void setAction(Command action)
    {
        this.action = action;
    }
}
