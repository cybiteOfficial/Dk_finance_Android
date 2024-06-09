package com.example.bankapp;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class CollateralDetailsActivity extends AppCompatActivity {
    EditText docs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collateral_details);

    }
}