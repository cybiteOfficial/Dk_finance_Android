package com.example.bankapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Check if user is already logged in
        if (isLoggedIn()) {
            // If user is already logged in, start DashboardActivity
            startDashboardActivity();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate username and password here if needed
                // For simplicity, let's assume they are valid
                // If you want to validate, uncomment the code in the validateUsername and validatePassword methods

                // For now, let's just start the DashboardActivity
                startDashboardActivity();
                // Mark user as logged in
                setLoggedIn(true);
            }
        });
    }

//    public Boolean validateUsername() {
//        String val = loginUsername.getText().toString();
//        if (val.isEmpty()) {
//            loginUsername.setError("Username cannot be empty");
//            return false;
//        } else {
//            loginUsername.setError(null);
//            return true;
//        }
//    }
//
//    public Boolean validatePassword(){
//        String val = loginPassword.getText().toString();
//        if (val.isEmpty()) {
//            loginPassword.setError("Password cannot be empty");
//            return false;
//        } else {
//            loginPassword.setError(null);
//            return true;
//        }
//    }

    private boolean isLoggedIn() {
        // Retrieve the login status from SharedPreferences
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void setLoggedIn(boolean loggedIn) {
        // Save the login status to SharedPreferences
        sharedPreferences.edit().putBoolean("isLoggedIn", loggedIn).apply();
    }

    private void startDashboardActivity() {
        // Start the DashboardActivity
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Finish LoginActivity so the user can't navigate back to it
    }
}
