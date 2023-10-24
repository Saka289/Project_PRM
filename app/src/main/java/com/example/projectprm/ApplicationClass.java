package com.example.projectprm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class ApplicationClass extends Application {

    public static final Object CHANNEL_ID = "channel1";
    public static final String PLAY = "play";
    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String EXIT = "exit";


    @Override
    public void onCreate() {

        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            NotificationChannel notificationChannel = new NotificationChannel((String) CHANNEL_ID, "Now Playing Song",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This is a important channel for showing song!!!");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
