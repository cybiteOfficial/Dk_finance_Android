package com.example.bankapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoanDetailsActivity extends AppCompatActivity {
    Spinner productType,customerType;
    EditText loanAmount,appliedTenure,appliedRoi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);
        loanAmount = findViewById(R.id.loanAmount);
        appliedRoi = findViewById(R.id.appliedRoi);
        appliedTenure = findViewById(R.id.appliedTenure);


        productType = findViewById(R.id.productType);
        customerType = findViewById(R.id.customerType);

        Spinner caseTagSpinner = findViewById(R.id.caseTag);
        ArrayAdapter<String> caseTagAdapter = new ArrayAdapter<String>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.case_tag)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }
        };
        caseTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caseTagSpinner.setAdapter(caseTagAdapter);


        Spinner productSpinner = findViewById(R.id.productType);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<String>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.product_types_array)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }
        };
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productAdapter);

        Spinner customerTypeSpinner = findViewById(R.id.customerType);
        ArrayAdapter<String> customerTypeAdapter = new ArrayAdapter<String>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.customer_type)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        customerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerTypeSpinner.setAdapter(customerTypeAdapter);

    }
}