package com.zjgsu.studentmanagement.Util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class myReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "One Minute", Toast.LENGTH_LONG).show();
    }
}
