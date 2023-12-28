package com.app.scheduleit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent intent1 = new Intent(context, textreceive.class);
        intent1.putExtra("Task",intent.getStringExtra("Task"));
        intent1.putExtra("Day",intent.getStringExtra("Day"));
        intent1.putExtra("Code",intent.getIntExtra("Code",0));
        Log.i("log2",String.valueOf(intent.getIntExtra("Code",0))+" "+intent.getStringExtra("Task"));
        context.startService(intent1);
    }
}
