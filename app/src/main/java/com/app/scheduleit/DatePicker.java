package com.app.scheduleit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class DatePicker extends DialogFragment {
    SQLiteDatabase mydatabase;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mydatabase = this.getActivity().openOrCreateDatabase("Tasks", MODE_PRIVATE, null);
        try {
            Cursor c = mydatabase.rawQuery("SELECT * FROM a3 WHERE task = ? AND day = ? and name = ?", new String[]{TaskDetails.TASK, TaskDetails.DAY, TaskDetails.user});
            int dayIndex = c.getColumnIndex("day1");
            int monthIndex = c.getColumnIndex("month");
            int yearIndex = c.getColumnIndex("year");
            c.moveToFirst();
            String st1 = c.getString(dayIndex);
            String st2= c.getString(monthIndex);
            String st3 = c.getString(yearIndex);
            int day1 = Integer.parseInt(st1);
            int month1 = Integer.parseInt(st2);
            int year1 = Integer.parseInt(st3);
            if(st1!=null)
            {
                return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year1, month1, day1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}
