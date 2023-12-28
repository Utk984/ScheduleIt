package com.app.scheduleit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

public class Action extends IntentService {

    public Action() {
        super(null);
    }

    SQLiteDatabase mydatabase;
    String user, task, day;
    int code;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mydatabase = this.openOrCreateDatabase("Tasks", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS a3 (task VARCHAR, name VARCHAR, day VARCHAR, checked VARCHAR, note VARCHAR, day1 VARCHAR, month VARCHAR, year VARCHAR, current VARCHAR, repeat VARCHARA, setTime VARCHAR, week INT(1))");
        user = intent.getStringExtra("User");
        task = intent.getStringExtra("Task");
        day = intent.getStringExtra("Day");
        code = intent.getIntExtra("Code",100);
        Log.i("log6",String.valueOf(code)+" "+task);
        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(code);
        mydatabase.execSQL("UPDATE a3 SET checked = 'yes' WHERE day = ? AND name = ? AND task = ?", new String[]{day, user, task});
        Intent intent1 = new Intent(this,MainScreen.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
    }
}
