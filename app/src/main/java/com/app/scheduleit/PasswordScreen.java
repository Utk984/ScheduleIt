package com.app.scheduleit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PasswordScreen extends AppCompatActivity{

    TextInputLayout current;
    TextInputLayout newPass;
    TextInputLayout confirmNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_screen);

        Toolbar toolbar = findViewById(R.id.password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.middle3);

        current = findViewById(R.id.current_password);
        newPass = findViewById(R.id.new_password);
        confirmNew = findViewById(R.id.confirm_password);
    }

    public void backtohome(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void changePass(View view)
    {
        if(!validateConfirm() | !validateCurrent() | !validatePassword())
            return;
        else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Change Password")
                    .setMessage("Are you sure you want to change your password?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseUser user = ParseUser.getCurrentUser();
                            user.setPassword(newPass.getEditText().getText().toString());
                            user.saveInBackground();
                            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            current.getEditText().setText("");
                            newPass.getEditText().setText("");
                            confirmNew.getEditText().setText("");
                        }
                    })
                    .show();
        }
    }

    boolean validateCurrent()
    {
        String passInput = current.getEditText().getText().toString().trim();
        if(passInput.isEmpty()){
            current.setError("Field cannot be empty");
            return false;
        }
        current.setError(null);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null) {
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) {
                            ParseUser.logInInBackground(user.getUsername(), current.getEditText().getText().toString(), new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (user == null)
                                        current.setError("Incorrect Password Entered");
                                }
                            });
                        }
                    }
                }
            }
        });
        if(current.getError()==null) {
            return true;
        }
        else {
            Log.i("HEhEH","not working");
            return false;
        }
    }

    boolean validatePassword()
    {
        String passInput = newPass.getEditText().getText().toString().trim();
        if(passInput.isEmpty()){
            newPass.setError("Field cannot be empty");
            return false;
        }
        else if(passInput.length()<8){
            newPass.setError("Password must contain at least 8 characters");
            return false;
        }
        newPass.setError(null);
        return true;
    }

    boolean validateConfirm()
    {
        String confirmInput = confirmNew.getEditText().getText().toString().trim();
        if(confirmInput.isEmpty()){
            confirmNew.setError("Field cannot be empty");
            return false;
        }
        else if(!(confirmInput.equals(newPass.getEditText().getText().toString().trim()))){
            confirmNew.setError("Passwords do not match");
            return false;
        }
        confirmNew.setError(null);
        return true;
    }

    public void forgotPass(View view)
    {
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordScreen.class);
        intent.putExtra("activity","profile");
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}