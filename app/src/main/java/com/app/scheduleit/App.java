package com.app.scheduleit;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.parse.Parse;

public class App extends Application {
    public static final String CHANNEL_5_ID = "Reminder";
    public static final String CHANNEL_6_ID = "Deadline";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());

        createChannels();
    }

    public void createChannels()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_5_ID,
                    "Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.enableVibration(true);
            channel1.setVibrationPattern(new long[]{500,300,500,300});
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            channel1.setSound(uri,null);
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_6_ID,
                    "Deadline",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.enableVibration(true);
            channel2.setVibrationPattern(new long[]{1000,500});
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }
    }
}
