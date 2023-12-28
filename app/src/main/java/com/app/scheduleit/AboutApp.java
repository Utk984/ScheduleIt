package com.app.scheduleit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AboutApp extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        Toolbar toolbar = findViewById(R.id.password_toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.middle4);

        linearLayout = findViewById(R.id.howto);
    }

    public void show(View view)
    {
        ImageButton imageButton = findViewById(R.id.dikhao);
        ImageView imageView = (ImageView)imageButton;
        if(imageView.getTag().equals("show")) {
            imageView.setImageResource(R.drawable.ic_arrowup);
            imageView.setTag("hide");
            ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
            params.height = 1;
            linearLayout.setLayoutParams(params);
        }
        else if(imageView.getTag().equals("hide")) {
            imageView.setImageResource(R.drawable.ic_show);
            imageView.setTag("show");
            ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            linearLayout.setLayoutParams(params);
        }
    }


    public void backtohome(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}