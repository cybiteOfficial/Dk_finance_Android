package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {

    private EditText otpEditText;
    private TextView phoneNumber;
    private EditText phoneNumberEditTxt;
    private EditText editTextDigit1;
    private EditText editTextDigit2;
    private EditText editTextDigit3;
    private EditText editTextDigit4;
    private Button submit_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        phoneNumber = findViewById(R.id.phoneNumber);
        phoneNumberEditTxt = findViewById(R.id.phoneNumberEditText);

        TextView changeNumber = findViewById(R.id.change_number);

        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber.setVisibility(View.GONE);
                phoneNumberEditTxt.setVisibility(View.VISIBLE);
            }
        });

        otpEditText = findViewById(R.id.editTextDigit1);

        editTextDigit1 = findViewById(R.id.editTextDigit1);
        editTextDigit2 = findViewById(R.id.editTextDigit2);
        editTextDigit3 = findViewById(R.id.editTextDigit3);
        editTextDigit4 = findViewById(R.id.editTextDigit4);

        setupEditTextListeners();

        submit_otp = findViewById(R.id.submit_otp);

        TextView change_number = findViewById(R.id.change_number);
        change_number.setPaintFlags(change_number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        TextView resend_btn = findViewById(R.id.resend_btn);
        resend_btn.setPaintFlags(change_number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        submit_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyOTP()) {
                    Intent intent = new Intent(OtpActivity.this, payment.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OtpActivity.this, "Incorrect OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean verifyOTP() {
        String enteredOTP = editTextDigit1.getText().toString().trim() +
                editTextDigit2.getText().toString().trim() +
                editTextDigit3.getText().toString().trim() +
                editTextDigit4.getText().toString().trim();

        String correctOTP = "1234";

        return enteredOTP.equals(correctOTP);
    }

    private void setupEditTextListeners() {
        editTextDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    editTextDigit2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    editTextDigit3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    editTextDigit4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
