package com.app.scheduleit;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.parse.ParseUser;

import static com.app.scheduleit.App.CHANNEL_5_ID;

public class textreceive extends IntentService {
    SQLiteDatabase mydatabase;
    String user, task, day;
    int code;

    public textreceive() {
        super(null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mydatabase = this.openOrCreateDatabase("Tasks", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS a3 (task VARCHAR, name VARCHAR, day VARCHAR, checked VARCHAR, note VARCHAR, day1 VARCHAR, month VARCHAR, year VARCHAR, current VARCHAR, repeat VARCHARA, setTime VARCHAR, week INT(1))");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS dayChange2 (date VARCHAR)");
        user = ParseUser.getCurrentUser().getUsername();
        task = intent.getStringExtra("Task");
        day = intent.getStringExtra("Day");
        code = intent.getIntExtra("Code",100);
        update();
    }

    public void noti1(String st)
    {
        Log.i("log4", code+" "+task);
        Intent intent = new Intent(this, MainScreen.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
              0, intent, 0);


        Intent intent1 = new Intent(this, NotificationAction.class);
        intent1.putExtra("Task",task);
        intent1.putExtra("Day",day);
        intent1.putExtra("User",user);
        intent1.putExtra("Code",code);
        PendingIntent contentIntent1 = PendingIntent.getBroadcast(this,
                code, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_5_ID)
                .setSmallIcon(R.drawable.ic_applogo)
                .setContentTitle("REMINDER")
                .setContentText("Task: "+st)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(contentIntent)
                .setColor(Color.argb(1, 79, 189, 239))
                .setAutoCancel(true)
                .setSound(sound)
                .addAction(R.drawable.ic_applogo,"Mark as done",contentIntent1)
                .setVibrate(new long[]{500,300,500,300});

        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        notification.notify(code, builder.build());
    }

    public void update()
    {
        try{
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE task = ? AND day = ? AND name = ? AND checked = 'no'", new String[]{task,day,user});
            int repeatIndex = c.getColumnIndex("repeat");
            int weekIndex = c.getColumnIndex("week");
            c.moveToFirst();
            String repeat = c.getString(repeatIndex);
            int count = c.getInt(weekIndex);
            if(repeat.equalsIgnoreCase("once")) {
                noti1(task);
                mydatabase.execSQL("UPDATE a3 SET setTime = '' WHERE day = ? AND name = ? AND task = ?", new String[]{day, user, task});
                mydatabase.execSQL("UPDATE a3 SET repeat = 'Never' WHERE day = ? AND name = ? AND task = ?", new String[]{day, user, task});
            }
            else if(repeat.equalsIgnoreCase("weekly")){
                if(count==0 || count==8){
                    noti1(task);
                    mydatabase.execSQL("UPDATE a3 SET week = 1 WHERE day = ? AND name = ? AND task = ?", new String[]{day, user, task});
                }
                else
                    mydatabase.execSQL("UPDATE a3 SET week = week+1 WHERE day = ? AND name = ? AND task = ?", new String[]{day, user, task});
            }
            else if(repeat.equalsIgnoreCase("daily"))
                noti1(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
