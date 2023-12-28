package com.app.scheduleit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationAction extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, Action.class);
        intent1.putExtra("Task",intent.getStringExtra("Task"));
        intent1.putExtra("Day",intent.getStringExtra("Day"));
        intent1.putExtra("Code",intent.getIntExtra("Code",100));
        Log.i("log5",String.valueOf(intent.getIntExtra("Code",100))+" "+intent.getStringExtra("Task"));
        intent1.putExtra("User",intent.getStringExtra("User"));
        context.startService(intent1);
    }
}
