package com.example.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    private Button newRegistrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        newRegistrationButton = findViewById(R.id.new_reg_btn);
        ImageView userProfileImageView = findViewById(R.id.user_profile);


        newRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start NewRegistrationActivity
                Intent intent = new Intent(DashboardActivity.this, NewRegistrationActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the user profile ImageView
        userProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the user profile dropdown menu
                showPopupMenu(v);
            }
        });

        // Add OnClickListener for other buttons if needed
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.user_profile_menu); // Inflate menu resource

        // Set name and ID directly as menu item titles
        popupMenu.getMenu().findItem(R.id.username).setTitle("Name: SampleUser");
        popupMenu.getMenu().findItem(R.id.employee_id).setTitle("Employee ID: 123456");




        popupMenu.show();
    }
}
