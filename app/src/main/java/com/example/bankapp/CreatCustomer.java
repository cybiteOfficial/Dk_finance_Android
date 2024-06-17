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
import java.util.Calendar;

public class CreatCustomer extends AppCompatActivity {
    private EditText dobEditText;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private EditText firstNameEditText;
    private EditText middleNameEditText;
    private EditText lastNameEditText;
    private EditText industryEditText;
    private EditText occupationEditText;
    private EditText sourceOfIncomeEditText;
    private EditText incomeEditText;
    private EditText familyIncomeEditText;
    private EditText ageEditText;
    private Spinner genderSpinner;
    private Spinner titleSpinner;
    private Spinner customerSegmentSpinner;
    private Spinner residenceOwnerSpinner;
    private Spinner agricultureLandOwnerSpinner;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_customer);

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
        industryEditText = findViewById(R.id.industry);
        occupationEditText = findViewById(R.id.occupation);
        sourceOfIncomeEditText = findViewById(R.id.sourceOfIncome);
        incomeEditText = findViewById(R.id.income);
        familyIncomeEditText = findViewById(R.id.familyIncome);
        ageEditText = findViewById(R.id.age);

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
        ArrayAdapter<String> residenceOwnerAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.customer_segment));
        residenceOwnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        residenceOwnerSpinner.setAdapter(residenceOwnerAdapter);

        agricultureLandOwnerSpinner = findViewById(R.id.agricultureLandOwner);
        ArrayAdapter<String> agricultureLandOwnerAdapter = new ArrayAdapter<>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.customer_segment));
        agricultureLandOwnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agricultureLandOwnerSpinner.setAdapter(agricultureLandOwnerAdapter);

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
            String industry = industryEditText.getText().toString().trim();
            String occupation = occupationEditText.getText().toString().trim();
            String sourceOfIncome = sourceOfIncomeEditText.getText().toString().trim();
            String income = incomeEditText.getText().toString().trim();
            String familyIncome = familyIncomeEditText.getText().toString().trim();
            String residenceOwner = residenceOwnerSpinner.getSelectedItem().toString();
            String agricultureLandOwner = agricultureLandOwnerSpinner.getSelectedItem().toString();

            Intent intent = new Intent(CreatCustomer.this, AddressActivity.class);
            intent.putExtra("customerName", firstName + " " + middleName + " " + lastName);
            intent.putExtra("dob", dob);
            intent.putExtra("age", age);
            intent.putExtra("gender", gender);
            intent.putExtra("title", title);
            intent.putExtra("customerSegment", customerSegment);
            intent.putExtra("industry", industry);
            intent.putExtra("occupation", occupation);
            intent.putExtra("sourceOfIncome", sourceOfIncome);
            intent.putExtra("income", income);
            intent.putExtra("familyIncome", familyIncome);
            intent.putExtra("residenceOwner", residenceOwner);
            intent.putExtra("agricultureLandOwner", agricultureLandOwner);
            intent.putExtra("imageFilePath", imagePath);

            Log.i("Customer Name", firstName + " " + middleName + " " + lastName);
            Log.i("DOB", dob);
            Log.i("Age", age);
            Log.i("imagePath", imagePath);

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
