package com.example.bankapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class CreatCustomer extends AppCompatActivity {
    private EditText dobEditText;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private EditText firstNameEditText;
    private EditText middleNameEditText;
    private EditText lastNameEditText;
    private EditText sourceOfIncomeEditText;
    private EditText incomeEditText;
    private EditText familyIncomeEditText;
    private EditText ageEditText;
    private EditText noOfDependentsEditText;
    private Spinner genderSpinner;
    private Spinner titleSpinner;
    private Spinner customerSegmentSpinner;
    private Spinner residenceOwnerSpinner;
    private Spinner agricultureLandOwnerSpinner;
    private Spinner roles;
    private Spinner educationQualificationSpinner;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_customer);

        Intent i = getIntent();
        String application_id = i.getStringExtra("application_id");
        Boolean applicantExists = i.getBooleanExtra("applicantExists", false);
        Log.i("Application ID", application_id);
        Log.i("Applicant Exists", String.valueOf(applicantExists));

        dobEditText = findViewById(R.id.dobEditText);
        imageView = findViewById(R.id.imageView);

        ImageView homeBtn = findViewById(R.id.homeButton);

        dobEditText.setOnClickListener(v -> showDatePickerDialog());
        Button btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Button submitBtn = findViewById(R.id.submit_button);

        firstNameEditText = findViewById(R.id.customerName);
        middleNameEditText = findViewById(R.id.customerMiddleName);
        lastNameEditText = findViewById(R.id.customerLastName);
        sourceOfIncomeEditText = findViewById(R.id.sourceOfIncome);
        incomeEditText = findViewById(R.id.income);
        familyIncomeEditText = findViewById(R.id.familyIncome);
        ageEditText = findViewById(R.id.age);
        noOfDependentsEditText = findViewById(R.id.noOfDependents);

        roles = findViewById(R.id.role);

        // Load roles from resources
        // Load roles from resources
        String[] rolesArray = getResources().getStringArray(R.array.application_roles);
        ArrayList rolesList = new ArrayList<>(Arrays.asList(rolesArray));

        // If applicant exists, remove "applicant" from roles list
        if (applicantExists) {
            rolesList.remove("Applicant"); // Ensure the casing matches exactly what is in the string array
        }

        // Create adapter with the updated roles list
        ArrayAdapter<String> rolesAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, rolesList);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roles.setAdapter(rolesAdapter);


        genderSpinner = findViewById(R.id.gender);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.gender_type));
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        titleSpinner = findViewById(R.id.title);
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.title_type));
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titleAdapter);

        customerSegmentSpinner = findViewById(R.id.customer_segment);
        ArrayAdapter<String> customerSegmentAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.customer_segment));
        customerSegmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerSegmentSpinner.setAdapter(customerSegmentAdapter);

        residenceOwnerSpinner = findViewById(R.id.residenceOwner);
        ArrayAdapter<String> residenceOwnerAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.residenceOwner));
        residenceOwnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        residenceOwnerSpinner.setAdapter(residenceOwnerAdapter);

        agricultureLandOwnerSpinner = findViewById(R.id.agricultureLandOwner);
        ArrayAdapter<String> agricultureLandOwnerAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.agricultureLandOwner));
        agricultureLandOwnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agricultureLandOwnerSpinner.setAdapter(agricultureLandOwnerAdapter);

        educationQualificationSpinner = findViewById(R.id.educationQualification);
        ArrayAdapter<String> educationQualificationAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.education_qualification));
        educationQualificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationQualificationSpinner.setAdapter(educationQualificationAdapter);

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CreatCustomer.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        submitBtn.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String middleName = middleNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String dob = dobEditText.getText().toString().trim();
            String age = ageEditText.getText().toString().trim();
            String gender = genderSpinner.getSelectedItem().toString();
            String title = titleSpinner.getSelectedItem().toString();
            String customerSegment = customerSegmentSpinner.getSelectedItem().toString();
            String sourceOfIncome = sourceOfIncomeEditText.getText().toString().trim();
            String income = incomeEditText.getText().toString().trim();
            String familyIncome = familyIncomeEditText.getText().toString().trim();
            String numberOfDependents = noOfDependentsEditText.getText().toString().trim();
            String residenceOwner = residenceOwnerSpinner.getSelectedItem().toString();
            String agricultureLandOwner = agricultureLandOwnerSpinner.getSelectedItem().toString();
            String educationQualification = educationQualificationSpinner.getSelectedItem().toString();
            String roleText = roles.getSelectedItem().toString();

            // Get the role from the selected role
            String role;
            if(roleText.equals("Applicant")) {
                role = "applicant";
            } else {
                role = "co_applicant";
            }

            Intent intent = new Intent(CreatCustomer.this, AddressActivity.class);

            // make a normal object of data and pass it to the next activity
            CustomerData customerData = new CustomerData(role, application_id, firstName, middleName , lastName , dob , age , gender, title, customerSegment, numberOfDependents
                    , sourceOfIncome, income, familyIncome, residenceOwner, agricultureLandOwner, educationQualification, imagePath);
            customerData.setRole(role);
            customerData.setApplicationId(application_id);
            customerData.setTitle(title);
            customerData.setFirstName(firstName);
            customerData.setMiddleName(middleName);
            customerData.setLastName(lastName);
            customerData.setDob(dob);
            customerData.setAge(age);
            customerData.setGender(gender);
            customerData.setCustomerSegment(customerSegment);
            customerData.setSourceOfIncome(sourceOfIncome);
            customerData.setIncome(income);
            customerData.setFamilyIncome(familyIncome);
            customerData.setNumberOfDependents(numberOfDependents);
            customerData.setResidenceOwner(residenceOwner);
            customerData.setAgricultureLandOwner(agricultureLandOwner);
            customerData.setEducationQualification(educationQualification);
            customerData.setImagePath(imagePath);

            // Send data to AddressActivity using Intent
            intent.putExtra("application_id", application_id);
            intent.putExtra("customerData", customerData);

            // check if the image path is not null
            if (imagePath != null) {
                Log.i("Image Path", imagePath);
            }
            startActivity(intent);
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.AppDatePickerDialog,
                (view, selectedYear, selectedMonth, selectedDay) -> dobEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    public void openFileChooser(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);  // Set the selected image to the ImageView

            // Get the file path from the URI
            imagePath = getRealPathFromURI(imageUri);
        }
    }

    // Helper method to get the real file path from URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        return filePath;
    }
}
