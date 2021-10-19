package com.example.all_in_won;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.w("AlarmReceiver", "-----------------------------------------------");
        Intent serviceIntent = new Intent(context, AlarmService.class);
        context.startService(serviceIntent);
    }
}
