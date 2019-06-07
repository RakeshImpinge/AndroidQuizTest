package com.androidquiz.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidquiz.R;

public class MyForeGroundService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    String channel_id = "my_channel_01";

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

    private void startForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
         final NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel_id);
         NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("you are on break");
        builder.setStyle(bigTextStyle);
        builder.setContentText("Content text");
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIconBitmap);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setFullScreenIntent(pendingIntent, true);
        final Notification notification = builder.build();

        new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished/1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;

                if(minutes > 0)
                    seconds = seconds % 60;
                if(hours > 0)
                    minutes = minutes % 60;
                String time = formatNumber(hours) + ":" + formatNumber(minutes) + ":" +
                        formatNumber(seconds);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MyForeGroundService.class), 0);
            if(Build.VERSION.SDK_INT>=26) {
                return new Notification.Builder(this)
                        .setContentTitle(title)
                        .setChannelId(channel_id)
                        .setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent).getNotification();
            }else{
                return new Notification.Builder(this)
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent).getNotification();
            }

    }

    private void stopForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");
        stopForeground(true);
        stopSelf();
    }


}