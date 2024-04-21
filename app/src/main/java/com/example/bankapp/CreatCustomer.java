package com.example.bankapp;

import android.graphics.Paint;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.List;

public class CreatCustomer extends AppCompatActivity {
    private EditText dobEditText;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button submit_btn;
    private ImageView imageView;

    private EditText firstNameEditText;
    private EditText middleNameEditText;
    private EditText lastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_customer);

        dobEditText = findViewById(R.id.dobEditText);
        imageView = findViewById(R.id.imageView);

        dobEditText.setOnClickListener(v -> showDatePickerDialog());
        Button btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        submit_btn = findViewById(R.id.submit_button);

        // building the full name

        firstNameEditText = findViewById(R.id.customerName);
        middleNameEditText = findViewById(R.id.customerMiddleName);
        lastNameEditText = findViewById(R.id.customerLastName);

        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String middleName = middleNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();

                String fullName = firstName + " " + middleName + " " + lastName;

                // Create an Intent to pass back the customer name
                Intent intent = new Intent(CreatCustomer.this, AddressActivity.class);
                intent.putExtra("customerName", fullName);

                // Get the existing co-applicant names from the previous activity
                List<String> coApplicantNames = getIntent().getStringArrayListExtra("coApplicantNames");

                // Create a new list to avoid modifying the original list
                List<String> updatedCoApplicantNames = new ArrayList<>();
                if (coApplicantNames != null) {
                    updatedCoApplicantNames.addAll(coApplicantNames); // Copy existing names
                }

                // Add the new customer name to the list
                updatedCoApplicantNames.add(fullName);

                // Log the co-applicant names before adding the new name
                Log.d("CreatCustomer", "Co-Applicant Names before adding: " + coApplicantNames);

                // Log the updated co-applicant names after adding the new name
                Log.d("CreatCustomer", "Co-Applicant Names after adding: " + updatedCoApplicantNames);

                // Pass the updated list back to the AddCustomerActivity
                intent.putStringArrayListExtra("coApplicantNames", new ArrayList<>(updatedCoApplicantNames));

                // Start the AddressActivity with the updated intent
                startActivity(intent);
            }
        });



    }


    private void showDatePickerDialog() {
        // Get current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog with the custom style
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.AppDatePickerDialog, // Apply custom style here
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date to the EditText
                    dobEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                },
                year,
                month,
                day
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
    public void openFileChooser(View view) {
        // Create a chooser intent to select either from gallery or capture from camera
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);

        // Create an intent to pick image from gallery
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Create an intent to capture image from camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Add the camera intent as an extra to the chooser intent
        chooserIntent.putExtra(Intent.EXTRA_INTENT, cameraIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Select Picture");

        // Put both intents into an intent array
        Intent[] intentArray = {galleryIntent};

        // Set the intent array as an extra for the chooser intent
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        // Start the chooser activity
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI from the intent
            Uri imageUri = data.getData();

            try {
                // Convert the image URI to a Bitmap and set it to the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            // If the result is from the camera intent
            // Get the captured image bitmap from the extras and set it to the ImageView
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }
}