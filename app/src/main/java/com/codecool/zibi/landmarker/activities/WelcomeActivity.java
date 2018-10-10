package com.codecool.zibi.landmarker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.codecool.zibi.landmarker.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void showLandmarks(View view){
        Intent intent = new Intent(this, MainActivity.class);
        int buttonID = view.getId();
        intent.putExtra(Intent.ACTION_CALL_BUTTON, buttonID);
        startActivity(intent);
    }
}
