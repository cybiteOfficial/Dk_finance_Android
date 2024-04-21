package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewRegistrationActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registration2);

        TextView btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Button continueButton = findViewById(R.id.submit_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewRegistrationActivity2.this, AddCustomerActivity.class));
            }
        });
    }
}