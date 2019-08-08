package com.example.groupmedia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int GALLERY_REQUEST_CODE = 100;
    ImageView getImage, userImage;
    Button save;
    EditText getName;
    public static final String PREFS_NAME = "LoginPrefs";
    Uri selectedImage = null;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!settings.getString("UserName", "" ).isEmpty() || !settings.getString("UserImage","").isEmpty()) {
            Intent intent = new Intent(this, MainScreenActivity.class);
            startActivity(intent);
        }

        userImage = findViewById(R.id.userImage);
        getImage = findViewById(R.id.getImage);
        save = findViewById(R.id.save);
        getName = findViewById(R.id.getName);

        getImage.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getImage:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
                break;
            case R.id.save:
                userName = getName.getText().toString();

                if (TextUtils.isEmpty(userName) ) {
                    Toast.makeText(this,"Please fill the field",Toast.LENGTH_SHORT).show();
                    return;
                }
                /*if(selectedImage==null)
                {
                    Toast.makeText(this,"Please fill the field",Toast.LENGTH_SHORT).show();
                    return;
                }*/

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("UserName", userName);
                if(selectedImage!=null)
                {

                    editor.putString("UserImage", selectedImage.toString());
                }
                else {
                    editor.putString("UserImage","");
                }
                editor.commit();
                Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, MainScreenActivity.class);
                startActivity(intent1);
                break;
        }

    }


    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == GALLERY_REQUEST_CODE) {
                selectedImage = data.getData();
                userImage.setImageURI(selectedImage);
            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
