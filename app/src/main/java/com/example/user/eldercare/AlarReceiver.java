package com.example.user.eldercare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Log.e(TAG, "收到");

        Toast.makeText(ctx, "Alarm received!", Toast.LENGTH_LONG).show();
//        Log.d(TAG, intent.toString());
//        String msg = intent.getStringExtra("msg");
//        Log.d(TAG, msg);
    }
}