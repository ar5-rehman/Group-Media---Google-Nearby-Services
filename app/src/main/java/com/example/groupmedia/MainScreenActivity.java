package com.example.groupmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener {

    Button audioBtn,videoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_screen);
        audioBtn = findViewById(R.id.audioBtn);
        videoBtn = findViewById(R.id.videoBtn);

        audioBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.audioBtn) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("check", "audio");
            startActivity(intent);
        }else if(v.getId()==R.id.videoBtn)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("check", "video");
            startActivity(intent);
        }
    }
}
