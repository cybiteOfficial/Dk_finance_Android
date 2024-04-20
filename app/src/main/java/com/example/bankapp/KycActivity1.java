package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class KycActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_1);

        // Adding underline to the "Save" button text
        TextView btnSave = findViewById(R.id.btn_save);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        setListeners();
    }

    private void setListeners() {
        // "Save" button listener
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // "Submit" button listener
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to KycActivity2
                startActivity(new Intent(KycActivity1.this, KycActivity2.class));
            }
        });

        // "Home" icon listener
        ImageView homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to DashboardActivity
                startActivity(new Intent(KycActivity1.this, DashboardActivity.class));
                finish();
            }
        });
    }
}
