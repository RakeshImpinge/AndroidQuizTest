package com.androidquiz;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.androidquiz.service.MyForeGroundService;

public class MainActivity extends AppCompatActivity {
    TextView text_production;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_production=(TextView)findViewById(R.id.text_production);


        if (BuildConfig.FLAVOR.equals("qa")){
            text_production.setText("QA");
            Log.e("APPLICATION_ID: ",BuildConfig.FLAVOR);
        } else {
            text_production.setText("PRODUCTION");
            Log.e("APPLICATION_ID: ",BuildConfig.FLAVOR);
        }

        //=====service=====
        if (isMyServiceRunning()){

        }
        else {
            setTitle("OPERR Driver");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(MainActivity.this, MyForeGroundService.class);
                intent.setAction(MyForeGroundService.ACTION_START_FOREGROUND_SERVICE);
                startForegroundService(intent);

            } else {
                Intent intent = new Intent(MainActivity.this, MyForeGroundService.class);
                intent.setAction(MyForeGroundService.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);
            }
        }

    }
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyForeGroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
