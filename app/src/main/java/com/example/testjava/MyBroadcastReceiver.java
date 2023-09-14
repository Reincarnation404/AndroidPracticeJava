package com.example.testjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("sender_name");
        Toast.makeText(context,"onReceive active, sender_name: "+name, Toast.LENGTH_LONG).show();
       // System.out.println("onReceive active, sender_name: "+name);
    }
}
