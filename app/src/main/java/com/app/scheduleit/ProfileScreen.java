package com.app.scheduleit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileScreen extends AppCompatActivity
{
    TextInputLayout name;
    TextInputLayout email;
    EditText editText;
    EditText text;
    ImageView imageView;
    SQLiteDatabase db1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        name = findViewById(R.id.username_change);
        email = findViewById(R.id.email_change);
        editText = name.getEditText();
        editText.setText(ParseUser.getCurrentUser().getUsername());
        text = email.getEditText();
        text.setText(ParseUser.getCurrentUser().getEmail());
        imageView = findViewById(R.id.profilePicture);
        db1 = this.openOrCreateDatabase("yes", Context.MODE_PRIVATE,null);
        db1.execSQL("create table if not exists imageTb ( a blob )");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.middle2);
        setDP();
    }

    public void backtohome(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void setDP()
    {
        try {
            Cursor c = db1.rawQuery("select * from imageTb", null);
            if (c.moveToLast()) {
                byte[] image = c.getBlob(0);
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                if(bmp!=null)
                    imageView.setImageBitmap(bmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveChanges(View view)
    {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_arrow_upward_24)
                .setTitle("Save Changes")
                .setMessage("Are you sure you want to make these changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String changedName = editText.getText().toString();
                        String changedEmail = text.getText().toString();
                        ParseUser user = ParseUser.getCurrentUser();
                        String n1 = user.getUsername();
                        String e1 = user.getEmail();
                        user.setUsername(changedName);
                        user.setEmail(changedEmail);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    if(e.getLocalizedMessage().endsWith("username."))
                                        name.setError(String.valueOf(e.getLocalizedMessage()));
                                    else
                                        email.setError(String.valueOf(e.getLocalizedMessage()));
                                    user.setUsername(n1);
                                    user.setEmail(e1);
                                }
                                else{
                                    editText.setText(changedName);
                                    text.setText(changedEmail);
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editText.setText(ParseUser.getCurrentUser().getUsername());
                        text.setText(ParseUser.getCurrentUser().getEmail());
                    }
                })
                .show();
    }

    public void changePicture(View view)
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            Uri image = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                imageView.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
                byte[] bytes = stream.toByteArray();
                ContentValues values = new ContentValues();
                values.put("a",bytes);
                db1.insert("imageTb", null,values);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}