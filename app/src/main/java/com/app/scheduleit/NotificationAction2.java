package com.app.scheduleit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationAction2 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, Action2.class);
        intent1.putExtra("Task",intent.getStringExtra("Task"));
        intent1.putExtra("Day",intent.getStringExtra("Day"));
        intent1.putExtra("Code",intent.getIntExtra("Code",100));
        intent1.putExtra("User",intent.getStringExtra("User"));
        context.startService(intent1);
    }
}
