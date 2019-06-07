package com.example.sukhdeepkaur.androidquiz.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.sukhdeepkaur.androidquiz.R;

import java.util.Iterator;
import java.util.List;

public class MyForeGroundService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_PAUSE = "ACTION_PAUSE";

    public static final String ACTION_PLAY = "ACTION_PLAY";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME ="Service" ;
    String id = "my_channel_01";
    private long timeRemaining = 0;

    public MyForeGroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:

                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private void startForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
         NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);

        // Make notification show big text.
         NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        bigTextStyle.setBigContentTitle("you are on break");
        builder.setStyle(bigTextStyle);

        builder.setContentText("Content text");

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIconBitmap);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);
        final Notification notification = builder.build();

        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(id, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
          //  channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished/1000;//convert to seconds
                long minutes = seconds / 60;//convert to minutes
                long hours = minutes / 60;//convert to hours

                if(minutes > 0)//if we have minutes, then there might be some remainder seconds
                    seconds = seconds % 60;//seconds can be between 0-60, so we use the % operator to get the remainder
                if(hours > 0)
                    minutes = minutes % 60;//similar to seconds
                String time = formatNumber(hours) + ":" + formatNumber(minutes) + ":" +
                        formatNumber(seconds);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(Build.VERSION.SDK_INT>=26) {
                    NotificationChannel channel = new NotificationChannel(id, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    mNotificationManager.createNotificationChannel(channel);
                }
                Notification notification = getMyActivityNotification("You are on break","Time Left: " + time);

                mNotificationManager.notify(1, notification);

            }

            public void onFinish() {
                Notification notification = getMyActivityNotification("Done!","");
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, notification);

                stopForegroundService();
            }

        }.start();

        startForeground(1, notification);
    }

    private String formatNumber(long value){
        if(value < 10)
            return "0" + value;
        return value + "";
    }


    private Notification getMyActivityNotification(String title,String contentText){
        // The PendingIntent to launch our activity if the user selects
        // this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MyForeGroundService.class), 0);

        return new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent).getNotification();
    }

    private void stopForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }


}