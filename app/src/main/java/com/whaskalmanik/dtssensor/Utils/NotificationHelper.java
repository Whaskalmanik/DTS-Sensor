package com.whaskalmanik.dtssensor.Utils;


import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.whaskalmanik.dtssensor.R;

import static com.whaskalmanik.dtssensor.Utils.App.CHANNEL_1_ID;
import static com.whaskalmanik.dtssensor.Utils.App.CHANNEL_2_ID;

public class NotificationHelper {
    private NotificationManagerCompat notificationManager;
    private Context context;

    public NotificationHelper(Context context)
    {
        notificationManager = NotificationManagerCompat.from(context);
        this.context=context;
    }

    public void popWarning()
    {
        String title=context.getResources().getString(R.string.warning_title_not);
        String text=context.getResources().getString(R.string.warning_des_not);
        Notification notification = new NotificationCompat.Builder(context,CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(1,notification);
    }
    public void popCritical()
    {
        String title=context.getResources().getString(R.string.critical_title_not);
        String text=context.getResources().getString(R.string.critical_des_not);
        Notification notification = new NotificationCompat.Builder(context,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_report_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(2,notification);
    }

}
