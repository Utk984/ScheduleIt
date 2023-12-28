package com.app.scheduleit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {
    TextInputLayout name;
    TextInputLayout email;
    TextInputLayout password;
    TextInputLayout confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ParseUser.getCurrentUser()!=null){
            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
            startActivity(intent);
        }

        //permission();

        name = findViewById(R.id.username_input);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        confirm = findViewById(R.id.confirm_input);

        confirm.setOnKeyListener(this);
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

    boolean validateEmail()
    {
        email.setError(null);
        String emailInput = email.getEditText().getText().toString().trim();

        if(emailInput.isEmpty()){
            email.setError("Field cannot be empty");
            return false;
        }
        int f=1;
        for(int i=0;i<emailInput.length();i++){
            char ch = emailInput.charAt(i);
            if(ch=='@')
                f=0;
        }
        if(f==1){
            email.setError("Enter a valid email");
            return false;
        }
        email.setError(null);
        return true;
    }

    boolean validateUsername()
    {
        String username = name.getEditText().getText().toString().trim();
        if(username.isEmpty()){
            name.setError("Field cannot be empty");
            return false;
        }
        name.setError(null);
        return true;
    }

    boolean validatePassword()
    {
        String passInput = password.getEditText().getText().toString().trim();
        if(passInput.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }
        else if(passInput.length()<8){
            password.setError("Password must contain at least 8 characters");
            return false;
        }
        password.setError(null);
        return true;
    }

    boolean validateConfirm()
    {
        String confirmInput = confirm.getEditText().getText().toString().trim();
        if(confirmInput.isEmpty()){
            confirm.setError("Field cannot be empty");
            return false;
        }
        else if(!(confirmInput.equals(password.getEditText().getText().toString().trim()))){
            confirm.setError("Passwords do not match");
            return false;
        }
        confirm.setError(null);
        return true;
    }

    public void SignUp(View view)
    {
        /*
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            permission();
        }
        else {*/
            Log.i("ho", "hi");
            if (!validateConfirm() | !validateEmail() | !validatePassword() | !validateUsername())
                return;
            Log.i("hi", "hi");
            ParseUser user = new ParseUser();
            user.setUsername(name.getEditText().getText().toString().trim());
            user.setEmail(email.getEditText().getText().toString().trim());
            user.setPassword(password.getEditText().getText().toString().trim());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        if (e.getLocalizedMessage().equals("Account already exists for this username."))
                            name.setError(e.getLocalizedMessage());
                        else
                            email.setError(e.getLocalizedMessage());
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        startActivity(intent);
                    }
                }
            });
       // }
    }

    public void LogIn(View view){
        /*
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            permission();
        }
        else {*/
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
    //    }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            SignUp(v);
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}