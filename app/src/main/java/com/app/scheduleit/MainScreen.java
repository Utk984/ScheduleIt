package com.app.scheduleit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hotchemi.android.rate.AppRate;

public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnKeyListener{
    //FF86E137
    NotificationManagerCompat notificationManagerCompat;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    static ListView listView, listView2, listView3, listView4, listDetails;
    static ArrayList<String> array, array2, array3, array4, arrayDetails;
    static ArrayAdapter<String> adapter, adapter2, adapter3, adapter4, adapterDetails;
    ImageView imageView10;
    EditText editText;
    static int ele_position;
    static String str, DAY, TASK, DAY2, user;
    SQLiteDatabase mydatabase, db1;
    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        user = ParseUser.getCurrentUser().getUsername();
        notificationManagerCompat = NotificationManagerCompat.from(this);

        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();

        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(3)
                .setRemindInterval(3)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.middle);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView name = (TextView)headerView.findViewById(R.id.drawer_name);
        TextView email = (TextView)headerView.findViewById(R.id.drawer_email);
        name.setText(user);
        email.setText(ParseUser.getCurrentUser().getEmail());

        editText = findViewById(R.id.taskText);
        imageView10 = (ImageView)headerView.findViewById(R.id.nav_pic);
        assign();
        db1 = this.openOrCreateDatabase("yes", Context.MODE_PRIVATE,null);
        db1.execSQL("create table if not exists imageTb ( a blob )");

        mydatabase = this.openOrCreateDatabase("Tasks", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS a3 (task VARCHAR, name VARCHAR, day VARCHAR, checked VARCHAR, note VARCHAR, day1 VARCHAR, month VARCHAR, year VARCHAR, current VARCHAR, repeat VARCHARA, setTime VARCHAR, week INT(1))");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS dayChange2 (date VARCHAR)");
        Cursor c = mydatabase.rawQuery("SELECT * FROM dayChange2", null);
        if(c.getCount()==0) {
            Date current = Calendar.getInstance().getTime();
            str = String.valueOf(current);
            str = str.substring(8, 10);
            mydatabase.execSQL("INSERT INTO dayChange2(date) VALUES (?)", new String[]{str});
        }
        call();
        reminder();
    }

    public void permission()
    {
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            new AlertDialog.Builder(this)
                    .setTitle("Allow app to run in background")
                    .setMessage("In order for the app to run perfectly and show notifications, you have to stop battery optimization for the app.\n\nYou will be redirected to the Settings page where you will have to search for \"Schedule It\" and then choose \"Dont optimize Battery Life\"\n\nThis is not harmful at all and will not affect your device.")
                    .setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    public void reminder()
    {
        try{
            x=0;
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE day != 'tomorrow' AND checked = 'no' AND name = ? AND checked = 'no'", new String[]{user});
            int repeatIndex = c.getColumnIndex("repeat");
            int timeIndex = c.getColumnIndex("setTime");
            int taskIndex = c.getColumnIndex("task");
            int dayIndex = c.getColumnIndex("day");
            c.moveToFirst();
            while(c!=null){
                String repeat = c.getString(repeatIndex);
                String time = c.getString(timeIndex);
                String task = c.getString(taskIndex);
                String day = c.getString(dayIndex);
                if(repeat.equalsIgnoreCase("never")) {
                    c.moveToNext();
                    continue;
                }
                int a = Integer.parseInt(time.substring(0,time.indexOf(':')));
                if(a==12)
                    a=0;
                int b = Integer.parseInt(time.substring(time.indexOf(':')+1,time.indexOf(' ')));
                Log.i("log1", String.valueOf(x)+" "+task+" "+time+" "+repeat);
                Intent intent = new Intent(this, AlertReceiver.class);
                intent.putExtra("Task",task);
                intent.putExtra("Day",day);
                intent.putExtra("Code",x);
                PendingIntent sender = PendingIntent.getBroadcast(this, x, intent,PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR,a);
                calendar.set(Calendar.MINUTE,b);
                calendar.set(Calendar.SECOND, 0);
                if(calendar.before(Calendar.getInstance()) && repeat.equalsIgnoreCase("daily"))
                    calendar.add(Calendar.DATE,1);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                x++;
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deadline()
    {
        x=0;
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE checked = 'no' AND name = ?", new String[]{user});
            int dayIndex = c.getColumnIndex("day1");
            int monthIndex = c.getColumnIndex("month");
            int taskIndex = c.getColumnIndex("task");
            int dayIndex1 = c.getColumnIndex("day");
            c.moveToFirst();
            while (c != null) {
                int day = Integer.parseInt(c.getString(dayIndex));
                int month = Integer.parseInt(c.getString(monthIndex))+1;
                String task = c.getString(taskIndex);
                String day1 = c.getString(dayIndex1);
                int diff = currentTime(day,month);
                if(diff<=7){
                    Intent intent = new Intent(this, AlertReceiver2.class);
                    intent.putExtra("Task",task);
                    intent.putExtra("Day",day1);
                    intent.putExtra("Code",x);
                    intent.putExtra("Left",diff);
                    PendingIntent sender = PendingIntent.getBroadcast(this, x, intent,PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), sender);
                    x++;
                }
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int currentTime(int d, int m)
    {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int day = Integer.parseInt(date.substring(0,2));
        int month = Integer.parseInt(date.substring(3,5));
        int buffer = 0;

        if(m==1 && month==12)
            m=13;

        if(month==2)
            buffer = 28;
        else if(month==4 || month==6 || month==9 || month==11)
            buffer = 30;
        else
            buffer = 31;
        if(d-7<=0) {
            d += buffer;
        }
        d-=7;
        int diff = day-d;
        if(m-month==1) {
            if (day >= d && diff<=7)
                return 7 - diff;
        }
        else if(month==m) {
            if (day >= d)
                return 7 - diff;
        }
        return 100;
    }

    public void call()
    {
        date();
        setDP();
        savedTasks("today", array, adapter, listView);
        savedTasks("tomorrow", array2, adapter2, listView2);
        savedTasks("later", array3, adapter3, listView3);
        savedTasks("incomplete", array4, adapter4, listView4);
        currentTasks(listView, "today", array, adapter);
        currentTasks(listView2, "tomorrow", array2, adapter2);
        currentTasks(listView3, "later", array3, adapter3);
        currentTasks(listView4, "incomplete", array4, adapter4);
    }

    public void assign()
    {
        listView = findViewById(R.id.today_tasks);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.check, array){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (listView.isItemChecked(position))
                    view.setBackgroundResource(R.drawable.shape);
                else
                    view.setBackgroundDrawable(null);
                return view;
            }
        };
        listView.setAdapter(adapter);

        listView2 = findViewById(R.id.tomorrow_tasks);
        array2 = new ArrayList<>();
        adapter2 = new ArrayAdapter<String>(this, R.layout.check, array2){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (listView2.isItemChecked(position))
                    view.setBackgroundResource(R.drawable.shape);
                else
                    view.setBackgroundDrawable(null);
                return view;
            }
        };
        listView2.setAdapter(adapter2);

        listView3 = findViewById(R.id.later_tasks);
        array3 = new ArrayList<>();
        adapter3 = new ArrayAdapter<String>(this, R.layout.check, array3){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (listView3.isItemChecked(position))
                    view.setBackgroundResource(R.drawable.shape);
                else
                    view.setBackgroundDrawable(null);
                return view;
            }
        };
        listView3.setAdapter(adapter3);

        listView4 = findViewById(R.id.incomplete_tasks);
        array4 = new ArrayList<>();
        adapter4 = new ArrayAdapter<String>(this, R.layout.check, array4){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (listView4.isItemChecked(position))
                    view.setBackgroundResource(R.drawable.shape);
                else
                    view.setBackgroundDrawable(null);
                return view;
            }
        };
        listView4.setAdapter(adapter4);
    }

    public void date()
    {
        Date current = Calendar.getInstance().getTime();
        str = String.valueOf(current);
        str = str.substring(8,10);
        String date = "";
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM dayChange2", null);
            int dayIndex = c.getColumnIndex("date");
            c.moveToFirst();
            date = c.getString(dayIndex);
        } catch (Exception e) {
            Log.i("error", String.valueOf(e));
        }
        if(!date.equals(str)){
            mydatabase.execSQL("UPDATE dayChange2 SET date = ?", new String[]{str});
            size();
            deadline();
        }
    }

    public void size()
    {
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE name = ?", new String[]{user});
            int dayIndex = c.getColumnIndex("day");
            int taskIndex = c.getColumnIndex("task");
            int checkedIndex = c.getColumnIndex("checked");
           // if (c.getCount() > 0)
             //   noti1("Incomplete Tasks");
            c.moveToFirst();
            while (c != null) {
                String checked = c.getString(checkedIndex);
                String day = c.getString(dayIndex);
                String task = c.getString(taskIndex);
                if(checked.equalsIgnoreCase("no")) {
                    if (day.equals("today"))
                        mydatabase.execSQL("UPDATE a3 SET day = 'incomplete' WHERE name = ? AND task = ?", new String[]{user, task});
                    if (day.equals("tomorrow"))
                        mydatabase.execSQL("UPDATE a3 SET day = 'today' WHERE name = ? AND task = ?", new String[]{user, task});
                }
                else if(checked.equalsIgnoreCase("yes"))
                    mydatabase.execSQL("DELETE FROM a3 WHERE name = ? AND task = ? AND day = ?", new String[]{user, task, day});
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savedTasks(String st, ArrayList<String> a, ArrayAdapter<String> b, ListView l)
    {
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE day = ? AND name = ? AND checked = 'no'", new String[]{st, user});
            int taskIndex = c.getColumnIndex("task");
            c.moveToFirst();
            while (c != null) {
                String task = c.getString(taskIndex);
                a.add(String.valueOf(task));
                b.notifyDataSetChanged();
                l.setItemChecked(a.size() - 1, false);
                changeSize(l, a, b);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE day = ? AND name = ? AND checked = 'yes'", new String[]{st, user});
            int taskIndex = c.getColumnIndex("task");
            c.moveToFirst();
            while (c != null) {
                String task = c.getString(taskIndex);
                a.add(String.valueOf(task));
                b.notifyDataSetChanged();
                l.setItemChecked(a.size() - 1, true);
                changeSize(l, a, b);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void plusTask(View view)
    {
        if(view.getTag().equals("today"))
            editText.setHint("Enter today's task");
        else if(view.getTag().equals("tomorrow"))
            editText.setHint("Enter tomorrow's task");
        else if(view.getTag().equals("later"))
            editText.setHint("Enter task");
        editText.requestFocus();
        editText.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public void addTask(View view)
    {
        String s = String.valueOf(editText.getHint());
        if(s.equals("Enter today's task"))
            saveTask("today", array, adapter, listView);
        else if(s.equals("Enter tomorrow's task"))
            saveTask("tomorrow", array2, adapter2, listView2);
        else if(s.equals("Enter task"))
            saveTask("later", array3, adapter3, listView3);
    }

    public void saveTask(String st, ArrayList<String> a, ArrayAdapter<String> b, ListView l)
    {
        String s = String.valueOf(editText.getText());
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE day = ? AND name = ? AND task = ?", new String[]{st, user, s});
            if (c.getCount() > 0) {
                c.close();
                Toast.makeText(MainScreen.this, "Task already added", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(s.isEmpty())
            return;
        a.add(s);
        b.notifyDataSetChanged();
        changeSize(l, a, b);
        editText.setText(null);
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        mydatabase.execSQL("INSERT INTO a3 (task, name, day, checked, note) VALUES (?,?,?,'no','')", new String[] {s, user, st});
    }

    public void currentTasks(ListView l, String day, ArrayList<String> a, ArrayAdapter<String> b)
    {
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(manager.isAcceptingText())
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

                ele_position = position;
                listDetails = l;
                DAY = day;
                DAY2 = day;
                TASK = String.valueOf(l.getItemAtPosition(position));
                arrayDetails = a;
                adapterDetails = b;

                Intent intent = new Intent(getApplicationContext(), TaskDetails.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,R.anim.no);

                return true;
            }
        });
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strike(l, view, position, day);
            }
        });
    }

    public void strike(ListView l, View view, int position, String s)
    {
        TextView textView = (TextView)view;
        String st = textView.getText().toString();
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE day = ? AND name = ? AND task = ?", new String[]{s, user, st});
            int checkedIndex = c.getColumnIndex("checked");
            c.moveToFirst();
            while(c!=null)
            {
                String str = c.getString(checkedIndex);
                if(l.isItemChecked(position)){
                    view.setBackgroundResource(R.drawable.shape);
                    mydatabase.execSQL("UPDATE a3 SET checked = 'yes' WHERE day = ? AND name = ? AND task = ?", new String[]{s, user, st});
                }
                else{
                    view.setBackgroundDrawable(null);
                    mydatabase.execSQL("UPDATE a3 SET checked = 'no' WHERE day = ? AND name = ? AND task = ?", new String[]{s, user, st});
                }
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeSize(ListView l, ArrayList<String> a, ArrayAdapter<String> b)
    {
        if (a.size() > 5) {
            ViewGroup.LayoutParams params = l.getLayoutParams();
            params.height = 750;
            l.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = l.getLayoutParams();
            params.height = getListViewHeightBasedOnChildren(l);
            l.setLayoutParams(params);
        }
        if(array4.size()>0) {
            TextView textView = findViewById(R.id.textView10);
            ImageButton button1 = findViewById(R.id.imageButton6);
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            textView.setLayoutParams(params);
            button1.setVisibility((int) 1);
        }
        else if(array4.size()==0){
            TextView textView = findViewById(R.id.textView10);
            ImageButton button1 = findViewById(R.id.imageButton6);
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            params.height = 1;
            textView.setLayoutParams(params);
            button1.setVisibility((int) -1);
        }
    }

    public void setDP()
    {
        try {
            Cursor c = db1.rawQuery("select * from imageTb", null);
            if(c.moveToLast()) {
                byte[] image = c.getBlob(0);
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageView10.setImageBitmap(bmp);
            }
        } catch (Exception e) {
            Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void hide(View view)
    {
        ListView l;
        ArrayList<String> a;
        ImageView imageView = (ImageView) view;
        if(imageView.getId()==findViewById(R.id.imageButton5).getId()){
            l = listView2; a = array2;
        }
        else if(imageView.getId()==findViewById(R.id.imageButton4).getId()) {
            l = listView; a = array;
        }
        else if(imageView.getId()==findViewById(R.id.imageButton10).getId()) {
            l = listView3; a = array3;
        }
        else{
            l = listView4; a = array4;
        }


        if(imageView.getTag().equals("show")) {
            imageView.setImageResource(R.drawable.ic_hide);
            imageView.setTag("hide");
            ViewGroup.LayoutParams params = l.getLayoutParams();
            params.height = 1;
            l.setLayoutParams(params);
        }
        else if(imageView.getTag().equals("hide")) {
            imageView.setImageResource(R.drawable.ic_show);
            imageView.setTag("show");
            l.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = l.getLayoutParams();
            if(a.size()>5)
                params.height = 750;
            else
                params.height = getListViewHeightBasedOnChildren(l);
            l.setLayoutParams(params);
        }

    }

    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch(item.getItemId())
        {
            case R.id.nav_account:
                Intent intent = new Intent(getApplicationContext(), ProfileScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_logout:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.logout)
                        .setTitle("LogOut")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseUser.logOut();
                                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .show();
                break;
            case R.id.nav_password:
                Intent intent1 = new Intent(getApplicationContext(), PasswordScreen.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_message:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                String[] TO = {"scheduleit.2021@gmail.com"};
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                try{
                    startActivity(Intent.createChooser(emailIntent,"Choose"));
                }catch (Exception e) {
                    Log.i("Error",e.getLocalizedMessage());
                }
                break;
            case R.id.nav_rate:
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="+ getPackageName())));
                }catch(ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="+ getPackageName())));
                }
                break;
            case R.id.nav_share:
                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                String shareBody = "Download this app: http://play.google.com/store/apps/details?id="+getPackageName();
                String shareSub = "Schedule It App";
                intent2.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                intent2.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(intent2, "ShareVia"));
                break;
            case R.id.nav_about:
                Intent intent5 = new Intent(getApplicationContext(), AboutApp.class);
                startActivity(intent5);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
            addTask(v);
        return false;
    }

    public int getListViewHeightBasedOnChildren(ListView list)
    {
        ListAdapter listAdapter = list.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0,len = listAdapter.getCount(); i < len; i++) {
            View listitem = listAdapter.getView(i,null,list);
            listitem.measure(0,0);
            totalHeight += listitem.getMeasuredHeight()+list.getDividerHeight()+40;
        }
        return totalHeight;
    }
}