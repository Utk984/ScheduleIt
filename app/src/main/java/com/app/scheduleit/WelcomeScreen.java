package com.app.scheduleit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

public class WelcomeScreen extends AppCompatActivity {

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        TextView text = findViewById(R.id.textView);
        text.animate().alpha(1).setDuration(1000);

        TextView textView = findViewById(R.id.textView2);
        String name = "";
        if(ParseUser.getCurrentUser()!=null) {
            name = ParseUser.getCurrentUser().getUsername();
            if(name.indexOf(' ')!=-1)
                name = name.substring(0, name.indexOf(' '));
        }
        textView.setText(name);
        textView.animate().alpha(1).setDuration(1000).setStartDelay(1000);
    }

    public void next(View view)
    {
        TextView text1 = findViewById(R.id.textView3);
        TextView text2 = findViewById(R.id.textView4);
        TextView text3 = findViewById(R.id.textView5);
        if(count==0){
            count++;
            text1.animate().alpha(1).setDuration(1000);
        }
        else if(count==1){
            count++;
            text2.animate().alpha(1).setDuration(1000);
        }
        else if(count==2){
            count++;
            text3.animate().alpha(1).setDuration(1000);
            Button button = (Button)view;
            button.setText("Get Started");
        }
        else if(count==3){
            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
    }
}