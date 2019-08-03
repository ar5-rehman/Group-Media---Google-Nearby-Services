package com.example.groupmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView createButton,joinButton;
    Button playMusic;
    String check,check1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        createButton = findViewById(R.id.createButton);
        joinButton = findViewById(R.id.joinButton);
        playMusic = findViewById(R.id.musicButton);

        Intent audioIntent = getIntent();
        check = audioIntent.getStringExtra("check");
        check1 = check;
        createButton.setOnClickListener(this);
        joinButton.setOnClickListener(this);
        playMusic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.createButton:
                if (check.equals("audio")) {
                    Intent i = new Intent(this,AudioActivity.class);
                    i.putExtra("create","create");
                    startActivity(i);
                } else {
                    Intent i = new Intent(this,VideoActivity.class);
                    i.putExtra("create","create");
                    startActivity(i);
                }
                break;
            case R.id.joinButton:
                if (check1.equals("audio")) {
                    Intent i = new Intent(this,AudioActivity.class);
                    i.putExtra("create","join");
                    startActivity(i);
                } else {
                    Intent i = new Intent(this,VideoActivity.class);
                    i.putExtra("create","join");
                    startActivity(i);
                }

                break;
            case R.id.musicButton:
                    Intent intentMusic = new Intent(this, MediaPlayerActivity.class);
                    startActivity(intentMusic);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
