package com.app.scheduleit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseUser;
import com.wx.wheelview.widget.WheelViewDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskDetails extends AppCompatActivity implements View.OnTouchListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    ListView listDetails;
    ArrayList<String> arrayDetails;
    ArrayAdapter<String> adapterDetails;
    TextView textView, time, dayyy;
    ConstraintLayout baseLayout;
    GridLayout gridLayout;
    int previousFingerPosition = 0, baseLayoutPosition = 0, ele_position, hour, min;
    boolean isClosing = false, isScrollingDown = false;
    static String DAY, TASK, DAY2, user;
    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        user = ParseUser.getCurrentUser().getUsername();

        baseLayout = findViewById(R.id.taskDetails);
        gridLayout = findViewById(R.id.gridLayout);
        textView = findViewById(R.id.deadlineDate);
        time = findViewById(R.id.timepick);
        dayyy = findViewById(R.id.daypick);
        textView.setText("Set Date");
        isClosing = false;
        ele_position = MainScreen.ele_position;
        listDetails = MainScreen.listDetails;
        DAY = MainScreen.DAY;
        DAY2 = MainScreen.DAY2;
        TASK = MainScreen.TASK;
        arrayDetails = MainScreen.arrayDetails;
        adapterDetails = MainScreen.adapterDetails;

        Button button = findViewById(R.id.hi);
        ImageButton button1 = findViewById(R.id.deadline);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment date = new com.app.scheduleit.DatePicker();
                date.show(getSupportFragmentManager(), "date picker");
            }
        });

        mydatabase = this.openOrCreateDatabase("Tasks", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS a3 (task VARCHAR, name VARCHAR, day VARCHAR, checked VARCHAR, note VARCHAR, day1 VARCHAR, month VARCHAR, year VARCHAR, current VARCHAR, repeat VARCHARA, setTime VARCHAR, week INT(1))");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS dayChange2 (date VARCHAR)");
        button.setOnTouchListener(this);
        reminderTime();
        showWheel();
        taskDetails(TASK);
    }

    public void reminderTime()
    {
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dayyy.getText().toString().equalsIgnoreCase("NEVER"))
                    return;
                DialogFragment timepicker = new TimePickerFragment();
                timepicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    public void showWheel()
    {
        dayyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WheelViewDialog dialog = new WheelViewDialog(TaskDetails.this);
                dialog.setTitle("Reminder")
                        .setItems(new String[]{"Never", "Once", "Daily", "Weekly"})
                        .setDialogStyle(Color.parseColor("#0033CC"))
                        .setCount(5)
                        .setLoop(false)
                        .setButtonText("OK")
                        .setOnDialogItemClickListener(new WheelViewDialog.OnDialogItemClickListener() {
                            @Override
                            public void onItemClick(int position, String s) {
                                dayyy.setText(s);
                                if(dayyy.getText().toString().equalsIgnoreCase("never"))
                                    time.setText("");
                            }
                        })
                        .show();
            }
        });
    }

    public void taskDetails(String str)
    {
        for(int i=0;i<4;i++)
            gridLayout.getChildAt(i).setBackgroundResource(R.drawable.round5);
        EditText editText1 = findViewById(R.id.edittext);
        editText1.setText(str);
        EditText editText2 = findViewById(R.id.taskNotes);
        editText2.setText(null);
        View view = gridLayout.findViewWithTag(DAY);
        view.setBackgroundResource(R.drawable.round6);
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE day = ? AND name = ? AND task = ?", new String[]{DAY, user, str});
            int noteIndex = c.getColumnIndex("note");
            int currentIndex = c.getColumnIndex("current");
            int repeatIndex = c.getColumnIndex("repeat");
            int timeIndex = c.getColumnIndex("setTime");
            c.moveToFirst();
            String notes = c.getString(noteIndex);
            String date = c.getString(currentIndex);
            String repeat = c.getString(repeatIndex);
            String getTime = c.getString(timeIndex);
            if(notes!=null)
                editText2.setText(notes);
            if(date!=null)
                textView.setText(date);
            if(repeat==null)
                dayyy.setText("Never");
            else {
                dayyy.setText(repeat);
                if(repeat.equalsIgnoreCase("Never"))
                    mydatabase.execSQL("UPDATE a3 SET setTime = '' WHERE day = ? AND name = ? AND task = ?", new String[]{DAY, user, TASK});
            }
            if(getTime!=null)
                time.setText(getTime);
        } catch (Exception e) {
            Log.i("hello",e.getLocalizedMessage());
        }
    }

    public void dayChange(View view)
    {
        for(int i=0;i<4;i++)
            gridLayout.getChildAt(i).setBackgroundResource(R.drawable.round5);
        view.setBackgroundResource(R.drawable.round6);
        DAY2 = view.getTag().toString();
    }

    public void saveDetails(View view)
    {
        EditText editText1 = findViewById(R.id.edittext);
        EditText editText2 = findViewById(R.id.taskNotes);
        if(editText1.getText().length()==0){
            Toast.makeText(this, "Task Cannot be Empty", Toast.LENGTH_LONG).show();
            return;
        }
        String task1 = editText1.getText().toString();
        String notes = editText2.getText().toString();
        String repeat = dayyy.getText().toString();
        String getTime = time.getText().toString();
        if (!repeat.equalsIgnoreCase("never")){
            if (getTime.isEmpty()) {
                Toast.makeText(TaskDetails.this, "Please select a time",Toast.LENGTH_LONG).show();
                time.performClick();
                return;
            }
        }
        String checked="",time="";
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE day = ? AND name = ? AND task = ?", new String[]{DAY, user, TASK});
            int checkedIndex = c.getColumnIndex("checked");
            int timeIndex = c.getColumnIndex("setTime");
            c.moveToFirst();
            while(c!=null){
                checked = c.getString(checkedIndex);
                time = c.getString(timeIndex);
                if(repeat.equalsIgnoreCase("weekly") && !time.equalsIgnoreCase(getTime))
                    mydatabase.execSQL("UPDATE a3 SET week = 0 WHERE day = ? AND name = ? AND task = ?", new String[]{DAY, user, TASK});
                mydatabase.execSQL("UPDATE a3 SET note = ? WHERE day = ? AND name = ? AND task = ?", new String[]{notes, DAY, user, TASK});
                mydatabase.execSQL("UPDATE a3 SET task = ? WHERE day = ? AND name = ? AND task = ?", new String[]{task1, DAY, user, TASK});
                mydatabase.execSQL("UPDATE a3 SET day = ? WHERE day = ? AND name = ? AND task = ?", new String[]{DAY2, DAY, user, TASK});
                mydatabase.execSQL("UPDATE a3 SET repeat = ? WHERE day = ? AND name = ? AND task = ?", new String[]{repeat, DAY, user, TASK});
                mydatabase.execSQL("UPDATE a3 SET setTime = ? WHERE day = ? AND name = ? AND task = ?", new String[]{getTime, DAY, user, TASK});
                if(repeat.equalsIgnoreCase("never"))
                    mydatabase.execSQL("UPDATE a3 SET setTime = '' WHERE day = ? AND name = ? AND task = ?", new String[]{DAY, user, TASK});
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TASK = task1;
        arrayDetails.set(ele_position, TASK);
        adapterDetails.notifyDataSetChanged();
        if(!DAY.equals(DAY2)){
            if(!listDetails.isItemChecked(ele_position+1))
                listDetails.setItemChecked(ele_position, false);
            arrayDetails.remove(ele_position);
            adapterDetails.notifyDataSetChanged();
            if(DAY2==null) {
            }
            else if(DAY2.equals("today")){
                MainScreen.array.add(TASK);
                MainScreen.adapter.notifyDataSetChanged();
                changeSize(MainScreen.listView, MainScreen.array, MainScreen.adapter);
                checkItem(checked, MainScreen.listView, MainScreen.array);
            }
            else if(DAY2.equals("tomorrow")){
                MainScreen.array2.add(TASK);
                MainScreen.adapter2.notifyDataSetChanged();
                changeSize(MainScreen.listView2, MainScreen.array2, MainScreen.adapter2);
                checkItem(checked, MainScreen.listView2, MainScreen.array2);
            }
            else if(DAY2.equals("later")){
                MainScreen.array3.add(TASK);
                MainScreen.adapter3.notifyDataSetChanged();
                changeSize(MainScreen.listView3, MainScreen.array3, MainScreen.adapter3);
                checkItem(checked, MainScreen.listView3, MainScreen.array3);
            }
            else if(DAY2.equals("incomplete")){
                MainScreen.array4.add(TASK);
                MainScreen.adapter4.notifyDataSetChanged();
                changeSize(MainScreen.listView4, MainScreen.array4, MainScreen.adapter4);
                checkItem(checked, MainScreen.listView4, MainScreen.array4);
            }
        }
        changeSize(listDetails, arrayDetails, adapterDetails);
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        startActivity(intent);
        overridePendingTransition(0,R.anim.slide_down);
    }

    public void checkItem(String s, ListView l, ArrayList<String> a)
    {
        if(s.equals("yes"))
            l.setItemChecked((a.size()-1),true);
        else
            l.setItemChecked((a.size()-1),false);
    }

    public void deleteTask(View view)
    {
        removeTask(ele_position, DAY, listDetails);
    }

    public void removeTask(int position, String st, ListView l)
    {
        new AlertDialog.Builder(TaskDetails.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mydatabase.execSQL("DELETE FROM a3 WHERE name = ? AND task = ? AND day = ?", new String[]{user,arrayDetails.get(position), st });
                        for(int i=position;i<arrayDetails.size();i++)
                        {
                            if(l.isItemChecked(i+1))
                                l.setItemChecked(i, true);
                            else
                                l.setItemChecked(i, false);
                        }
                        arrayDetails.remove(position);
                        adapterDetails.notifyDataSetChanged();
                        changeSize(l, arrayDetails, adapterDetails);
                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        startActivity(intent);
                        overridePendingTransition(0 ,R.anim.slide_down);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        startActivity(intent);
                        overridePendingTransition(0 ,R.anim.slide_down);
                    }
                })
                .show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                previousFingerPosition = Y;
                baseLayoutPosition = (int) baseLayout.getY();
                break;

            case MotionEvent.ACTION_UP:
                if(isScrollingDown && !isClosing){
                    baseLayout.setY(baseLayoutPosition);
                    isScrollingDown = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(!isClosing){
                    if(previousFingerPosition > Y){
                        baseLayout.setY(baseLayoutPosition);
                    }
                    else{
                        if(!isScrollingDown){
                            isScrollingDown = true;
                        }
                        if (Math.abs(previousFingerPosition-Y) > previousFingerPosition*2) {
                            isClosing = true;
                            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                            startActivity(intent);
                            overridePendingTransition(0,R.anim.slide_down);
                        }
                        baseLayout.setY(Y);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mydatabase.execSQL("UPDATE a3 SET day1 = ? WHERE task = ? AND day = ? AND name = ?",new String[]{String.valueOf(dayOfMonth), TASK, DAY, user});
        mydatabase.execSQL("UPDATE a3 SET month = ? WHERE task = ? AND day = ? AND name = ?",new String[]{String.valueOf(month), TASK, DAY, user});
        mydatabase.execSQL("UPDATE a3 SET year = ? WHERE task = ? AND day = ? AND name = ?",new String[]{String.valueOf(year), TASK, DAY, user});
        String current = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        textView.setText(current);
        mydatabase.execSQL("UPDATE a3 SET current = ? WHERE task = ? AND day = ? AND name = ?",new String[]{current, TASK, DAY, user});
        int diff = currentTime(dayOfMonth,month+1);
        if(diff<=7){
            Intent intent = new Intent(this, AlertReceiver2.class);
            intent.putExtra("Task",TASK);
            intent.putExtra("Day",DAY);
            intent.putExtra("Code",1000);
            intent.putExtra("Left",diff);
            PendingIntent sender = PendingIntent.getBroadcast(this, 1000, intent,PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), sender);
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

    @Override
    public void onBackPressed()
    {
        if(discard()){
            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
            startActivity(intent);
            overridePendingTransition(0,R.anim.slide_down);
            return;
        }
        else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle("Changes not saved")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Button button = findViewById(R.id.saveDetails);
                            button.performClick();
                        }
                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                            startActivity(intent);
                            overridePendingTransition(0,R.anim.slide_down);
                        }
                    })
                    .show();
        }
    }

    public boolean discard()
    {
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE day = ? AND name = ? AND task = ?", new String[]{DAY, user, TASK});
            EditText editText1 = findViewById(R.id.edittext);
            EditText editText2 = findViewById(R.id.taskNotes);
            TextView edittext3 = findViewById(R.id.deadlineDate);
            String task1 = editText1.getText().toString();
            String notes = editText2.getText().toString();
            String repeat = dayyy.getText().toString();
            String getTime = time.getText().toString();
            String date1 = edittext3.getText().toString();
            int taskIndex = c.getColumnIndex("task");
            int noteIndex = c.getColumnIndex("note");
            int currentIndex = c.getColumnIndex("current");
            int repeatIndex = c.getColumnIndex("repeat");
            int timeIndex = c.getColumnIndex("setTime");
            c.moveToFirst();
            String task2 = c.getString(taskIndex);
            String notes1 = c.getString(noteIndex);
            String date = c.getString(currentIndex);
            String repeat1 = c.getString(repeatIndex);
            String getTime1 = c.getString(timeIndex);
            int z=0;
            if(date==null && repeat1==null && getTime1==null && repeat.equalsIgnoreCase("never") && date1.equalsIgnoreCase("set date") && getTime=="")
                z=1;
            if(task1.equals(task2) && notes.equals(notes1) && DAY.equals(DAY2) && (date.equals(date1) && repeat.equals(repeat1) && getTime.equals(getTime1))) {
                return true;
            }
            else if(task1.equals(task2) && notes.equals(notes1) && DAY.equals(DAY2) && z==1)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        hour = hourOfDay;
        min = minute;
        String stime = hour+":"+min;
        SimpleDateFormat f24hours = new SimpleDateFormat("HH:mm");
        try {
            Date date = f24hours.parse(stime);
            SimpleDateFormat f12hours = new SimpleDateFormat("hh:mm aa");
            time.setText(f12hours.format(date));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public int currentTime(int d,int m)
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
        Log.i("hello",day+" "+d+" "+diff+" "+m+" "+month);
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
}