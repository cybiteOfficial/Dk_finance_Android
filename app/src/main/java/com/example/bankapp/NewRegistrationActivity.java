//package com.example.bankapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class NewRegistrationActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_registration);
//
//        Button generateOTPButton = findViewById(R.id.submit_button);
//        generateOTPButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Redirecting to OTPActivity
//                startActivity(new Intent(NewRegistrationActivity.this, KycActivity1.class));
//            }
//        });
//    }
//}

package com.example.bankapp;
import android.content.Intent;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class NewRegistrationActivity extends AppCompatActivity {

    EditText customerName, phoneNumber, agentCode, branchCode, branchName, loanAmount, date, time;
    Spinner caseTag, productType, customerType;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registration);

        customerName = findViewById(R.id.customerName);
        phoneNumber = findViewById(R.id.phoneNumber);
        agentCode = findViewById(R.id.agentCode);
        branchCode = findViewById(R.id.branchCode);
        branchName = findViewById(R.id.branchName);
        loanAmount = findViewById(R.id.loanAmount);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        caseTag = findViewById(R.id.caseTag);
        productType = findViewById(R.id.productType);
        customerType = findViewById(R.id.customerType);

        // Auto-fetch current date and time
        autofetchDate();
        autofetchTime();

        Button submitButton = findViewById(R.id.submit_button);
        Button btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Proceed with registration
                    startActivity(new Intent(NewRegistrationActivity.this, KycActivity1.class));
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void autofetchDate() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        // Set the current date in the EditText
        date.setText(currentDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void autofetchTime() {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String currentTime = timeFormat.format(calendar.getTime());

        // Set the current time in the EditText
        time.setText(currentTime);
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(customerName.getText().toString().trim())) {
            showMessage("Please enter customer name");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber.getText().toString().trim())) {
            showMessage("Please enter phone number");
            return false;
        }

        if (TextUtils.isEmpty(agentCode.getText().toString().trim())) {
            showMessage("Please enter agent code");
            return false;
        }

        if (TextUtils.isEmpty(branchCode.getText().toString().trim())) {
            showMessage("Please enter branch code");
            return false;
        }

        if (TextUtils.isEmpty(branchName.getText().toString().trim())) {
            showMessage("Please enter branch name");
            return false;
        }

        if (TextUtils.isEmpty(loanAmount.getText().toString().trim())) {
            showMessage("Please enter loan amount");
            return false;
        }

        if (TextUtils.isEmpty(date.getText().toString().trim())) {
            showMessage("Please enter date");
            return false;
        }

        if (TextUtils.isEmpty(time.getText().toString().trim())) {
            showMessage("Please enter time");
            return false;
        }

        // Add validation for other fields as needed

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
