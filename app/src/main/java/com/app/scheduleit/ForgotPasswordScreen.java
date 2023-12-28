package com.app.scheduleit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.List;

public class ForgotPasswordScreen extends AppCompatActivity {

    TextInputLayout em;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_screen);

        em = findViewById(R.id.forreset);
        String email = em.getEditText().getText().toString();
    }

    public void passwordReset(View view)
    {
        em.setError(null);
        String email = em.getEditText().getText().toString();
        check(email);
    }

    public void check(String email)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email",email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(objects.size()==0)
                   send(false);
                else
                    send(true);
            }
        });
    }

    public void send(boolean result)
    {
        String email = em.getEditText().getText().toString();
        if(result==false)
        {
            em.setError("Account does not exist");
            return;
        }
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
                    Log.i("Reset", "Successful");
                    Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Check your email for Reset Instructions", Toast.LENGTH_SHORT).show();
                }
                else
                    em.setError(e.getLocalizedMessage());
            }
        });
    }

    public void goback(View view)
    {
        Intent receive = getIntent();
        String activity = receive.getStringExtra("activity");
        if(activity.equals("profile")) {
            Intent intent = new Intent(getApplicationContext(), PasswordScreen.class);
            startActivity(intent);
        }
        else if(activity.equals("login"))
        {
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
        }
    }
}