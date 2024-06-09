package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    OkHttpClient client;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_userID);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        client = new OkHttpClient();
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Initialize the ProgressBar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the button before attempting login
                loginButton.setEnabled(false);
                // Proceed with validation and login attempt
                validateAndLogin();
            }
        });

    }

    private void validateAndLogin() {
        String username = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            loginUsername.setError("Username is required");
            // Enable the button again since there's an error
            loginButton.setEnabled(true);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Password is required");
            // Enable the button again since there's an error
            loginButton.setEnabled(true);
            return;
        }

        // Validation passed, proceed with login
        onClickPost(username, password);
    }
    public void onClickPost(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);
        String url = BASE_URL + "auth/signin";

        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestBody postBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(postBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);

                Response response = null;

                // Inside your try-catch block
                try {
                    response = call.execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String serverResponse = response.body().string();

                        Gson gson = new Gson();
                        LoginResponse loginResponse = gson.fromJson(serverResponse, LoginResponse.class);
                        Log.d("login log", String.valueOf(loginResponse));
                        // Check if the response contains an error
                        if (!loginResponse.isError() && loginResponse.getData() != null) {
                            String accessToken = loginResponse.getData().getAccessToken();

                            // Save access token in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("accessToken", accessToken);
                            editor.apply();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    // Proceed with the login process (e.g., start DashboardActivity)
                                    Intent mainIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                                    mainIntent.putExtra("userId", username);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loginButton.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                    // Clear the password field
                                    loginUsername.setText("");
                                    loginPassword.setText("");
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Enable the login button
                                loginButton.setEnabled(true);
                                // Show error message
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                // Clear the password field
                                loginUsername.setText("");
                                loginPassword.setText("");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Enable the login button
                            loginButton.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

        }).start();


    }
}

class LoginResponse {
    private boolean error;
    private Data data;
    private String message;

    public boolean isError() {
        return error;
    }

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    static class Data {
        private String access_token;
        private int expires_in;
        private String token_type;
        private String scope;
        private String refresh_token;

        public String getAccessToken() {
            return access_token;
        }

        public int getExpiresIn() {
            return expires_in;
        }

        public String getTokenType() {
            return token_type;
        }

        public String getScope() {
            return scope;
        }

        public String getRefreshToken() {
            return refresh_token;
        }
    }
}