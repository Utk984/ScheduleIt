package com.app.scheduleit;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.parse.ParseUser;

import static com.app.scheduleit.App.CHANNEL_6_ID;

public class textreceive2 extends IntentService {
    String user, task, day;
    int code,left;
    public textreceive2() {
        super(null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String st="";
        task = intent.getStringExtra("Task");
        day = intent.getStringExtra("Day");
        code = intent.getIntExtra("Code",100);
        user = ParseUser.getCurrentUser().getUsername();
        left = intent.getIntExtra("Left",100);
        if(left>1)
            st = left+" days left until deadline";
        else if(left==1)
            st = "Hurry Up!! tomorrow is the deadline";
        else if(left==0)
            st = "Today is the deadline!! Complete your task FAST";
        noti1(st);
    }

    public void noti1(String st)
    {
        Intent intent = new Intent(this, MainScreen.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, intent, 0);

        Intent intent1 = new Intent(this, NotificationAction2.class);
        intent1.putExtra("Task",task);
        intent1.putExtra("Day",day);
        intent1.putExtra("User",user);
        intent1.putExtra("Code",code);
        PendingIntent contentIntent1 = PendingIntent.getBroadcast(this,
                code, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_6_ID)
                .setSmallIcon(R.drawable.ic_applogo)
                .setContentTitle("DEADLINE: "+task)
                .setContentText(st)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(contentIntent)
                .setColor(Color.argb(1, 79, 189, 239))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_applogo,"Mark as done",contentIntent1)
                .setVibrate(new long[]{1000,500});

        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        notification.notify(code, builder.build());
    }
}
