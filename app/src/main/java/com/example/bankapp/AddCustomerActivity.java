package com.example.bankapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddCustomerActivity extends AppCompatActivity {

    private static final String TAG = "AddCustomerActivity";
    private LinearLayout coApplicantsLayout;
    ImageView homeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);


        TextView btnSave = findViewById(R.id.save_button);
        homeBtn = findViewById(R.id.homeButton);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        coApplicantsLayout = findViewById(R.id.coApplicantsLayout);


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCustomerActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Get the co-applicant names passed from AddressActivity
        ArrayList<String> coApplicantNames = getIntent().getStringArrayListExtra("coApplicantNames");

        if (coApplicantNames != null) {
            // Display co-applicant names
            displayCoApplicantNames(coApplicantNames);
        } else {
            Log.d(TAG, "No co-applicant names received.");
        }

        // Set click listener for the button
        findViewById(R.id.addCustomerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to CreatCustomer activity
                Intent intent = new Intent(AddCustomerActivity.this, CreatCustomer.class);
                intent.putStringArrayListExtra("coApplicantNames", coApplicantNames);
                startActivity(intent);
            }
        });
    }

    private void displayCoApplicantNames(ArrayList<String> coApplicantNames) {
        int i = 0;

        for (String name : coApplicantNames) {
            TextView titleName = new TextView(this);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            titleParams.setMargins(0, 10, 0, 8);
            titleName.setLayoutParams(titleParams);
            titleName.setTextColor(getResources().getColor(R.color.primary));
            if(i == 0){
                titleName.setText("Applicant Name");
            }
            else{
                titleName.setText("Co-Applicant " + i + " Name");
            }
            titleName.setTextSize(18);

            TextView applicantName = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            nameParams.setMargins(0, 16, 0, 20);
            nameParams.height = 160;
            applicantName.setLayoutParams(nameParams);
            applicantName.setTextColor(Color.parseColor("#000000"));
            applicantName.setText(name);
            applicantName.setBackgroundResource(R.drawable.edit_text_border);
            applicantName.setPadding(18, 12, 18, 12);
            applicantName.setTextSize(20);
            applicantName.setGravity(Gravity.CENTER_VERTICAL);


            coApplicantsLayout.addView(titleName);
            coApplicantsLayout.addView(applicantName);

            i++;
        }
    }


}
