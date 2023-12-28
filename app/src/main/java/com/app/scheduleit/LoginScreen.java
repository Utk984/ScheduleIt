package com.app.scheduleit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class LoginScreen extends AppCompatActivity {

    TextInputLayout email;
    TextInputLayout password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        ParseUser.logOut();

        email = findViewById(R.id.email_enter);
        password = findViewById(R.id.password_input);
    }

    boolean validateEmail()
    {
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

    boolean validatePassword()
    {
        String passInput = password.getEditText().getText().toString().trim();
        if(passInput.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }
        password.setError(null);
        return true;
    }

    public void LogIn(View view)
    {
        if(!validateEmail() | !validatePassword())
            return;
        String em = email.getEditText().getText().toString();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email",em);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null) {
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) {
                            ParseUser.logInInBackground(user.getUsername(), password.getEditText().getText().toString(), new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (user == null)
                                        password.setError("Incorrect Password Entered");
                                    else{
                                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                    else {
                        email.setError("Email does not exist");
                        password.setError(null);
                    }
                }
            }
        });
    }

    public void SignUp(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void forgotPass(View view)
    {
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordScreen.class);
        intent.putExtra("activity","login");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}