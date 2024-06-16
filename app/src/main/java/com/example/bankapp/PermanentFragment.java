package com.example.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class PermanentFragment extends Fragment {

    private EditText propertyOwnerEditText;
    private EditText propertyCategoryEditText;
    private EditText typesOfCategoryEditText;
    private EditText plotNumberEditText;
    private EditText localityEditText;
    private EditText villageEditText;
    private EditText stateEditText;
    private EditText districtEditText;
    private EditText cityEditText;
    private EditText talukaEditText;
    private EditText pinCodeEditText;
    private EditText landmarkEditText;
    private EditText propertyValueEditText;

    private CurrentAddressData currentAddressData;

    private boolean isSameAsCurrent = false; // Track if "Same as Current Address" is toggled

    LinearLayout sameAsCurrentLayout;
    Button submitBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_permanent, container, false);

        // Initialize EditText fields
        propertyOwnerEditText = rootView.findViewById(R.id.propertyOwner);
        propertyCategoryEditText = rootView.findViewById(R.id.propertyCategory);
        typesOfCategoryEditText = rootView.findViewById(R.id.typesOfCategory);
        plotNumberEditText = rootView.findViewById(R.id.plotNumber);
        localityEditText = rootView.findViewById(R.id.locality);
        villageEditText = rootView.findViewById(R.id.village);
        stateEditText = rootView.findViewById(R.id.state);
        districtEditText = rootView.findViewById(R.id.district);
        cityEditText = rootView.findViewById(R.id.city);
        talukaEditText = rootView.findViewById(R.id.taluka);
        pinCodeEditText = rootView.findViewById(R.id.pinCode);
        landmarkEditText = rootView.findViewById(R.id.landmark);
        propertyValueEditText = rootView.findViewById(R.id.propertyValue);

        // Initialize LinearLayout
        sameAsCurrentLayout = rootView.findViewById(R.id.sameAsCurrentLayout);

        // Initialize Button
        submitBtn = rootView.findViewById(R.id.submit_button);

        sameAsCurrentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSameAsCurrent) {
                    clearAndEnableFields();
                    isSameAsCurrent = false;
                } else {
                    if (currentAddressData != null) {
                        updateUIWithCurrentData(currentAddressData);
                        isSameAsCurrent = true;
                    }
                }
            }
        });

        // Set click listener for the button
        submitBtn.setOnClickListener(v -> {
            // Get data from EditText fields
            String propertyOwner = propertyOwnerEditText.getText().toString();
            String propertyCategory = propertyCategoryEditText.getText().toString();
            String typesOfCategory = typesOfCategoryEditText.getText().toString();
            String plotNumber = plotNumberEditText.getText().toString();
            String locality = localityEditText.getText().toString();
            String village = villageEditText.getText().toString();
            String state = stateEditText.getText().toString();
            String district = districtEditText.getText().toString();
            String city = cityEditText.getText().toString();
            String taluka = talukaEditText.getText().toString();
            String pinCode = pinCodeEditText.getText().toString();
            String landmark = landmarkEditText.getText().toString();
            String propertyValue = propertyValueEditText.getText().toString();

            // Create an Intent to pass back the data
            Intent intent = new Intent(getActivity(), AddCustomerActivity.class);
            // Get the customer name from the intent
            Intent parentIntent = getActivity().getIntent();
            String customerName = parentIntent.getStringExtra("customerName");
            ArrayList<String> coApplicantNames = parentIntent.getStringArrayListExtra("coApplicantNames");

            // Pass the customer name and co-applicant names
            intent.putExtra("customerName", customerName);
            intent.putStringArrayListExtra("coApplicantNames", coApplicantNames);

            startActivity(intent);
        });

        return rootView;
    }

    // Method to store the current address data
    public void updateData(CurrentAddressData currentAddressData) {
        this.currentAddressData = currentAddressData;
    }

    // Method to update UI with CurrentAddressData
    private void updateUIWithCurrentData(CurrentAddressData currentAddressData) {
        if (currentAddressData != null) {
            propertyOwnerEditText.setText(currentAddressData.getPropertyOwner());
            propertyCategoryEditText.setText(currentAddressData.getPropertyCategory());
            typesOfCategoryEditText.setText(currentAddressData.getTypesOfCategory());
            plotNumberEditText.setText(currentAddressData.getPlotNumber());
            localityEditText.setText(currentAddressData.getLocality());
            villageEditText.setText(currentAddressData.getVillage());
            stateEditText.setText(currentAddressData.getState());
            districtEditText.setText(currentAddressData.getDistrict());
            cityEditText.setText(currentAddressData.getCity());
            talukaEditText.setText(currentAddressData.getTaluka());
            pinCodeEditText.setText(currentAddressData.getPinCode());
            landmarkEditText.setText(currentAddressData.getLandmark());
            propertyValueEditText.setText(currentAddressData.getPropertyValue());

            // Make EditText fields non-editable
            makeFieldsNonEditable();
        } else {
            Log.e("PermanentFragment", "CurrentAddressData object is null.");
        }
    }

    private void makeFieldsNonEditable() {
        propertyOwnerEditText.setEnabled(false);
        propertyOwnerEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        propertyOwnerEditText.setTextColor(getResources().getColor(R.color.black));

        propertyCategoryEditText.setEnabled(false);
        propertyCategoryEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        propertyCategoryEditText.setTextColor(getResources().getColor(R.color.black));

        typesOfCategoryEditText.setEnabled(false);
        typesOfCategoryEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        typesOfCategoryEditText.setTextColor(getResources().getColor(R.color.black));

        plotNumberEditText.setEnabled(false);
        plotNumberEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        plotNumberEditText.setTextColor(getResources().getColor(R.color.black));

        localityEditText.setEnabled(false);
        localityEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        localityEditText.setTextColor(getResources().getColor(R.color.black));

        villageEditText.setEnabled(false);
        villageEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        villageEditText.setTextColor(getResources().getColor(R.color.black));

        stateEditText.setEnabled(false);
        stateEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        stateEditText.setTextColor(getResources().getColor(R.color.black));

        districtEditText.setEnabled(false);
        districtEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        districtEditText.setTextColor(getResources().getColor(R.color.black));

        cityEditText.setEnabled(false);
        cityEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        cityEditText.setTextColor(getResources().getColor(R.color.black));

        talukaEditText.setEnabled(false);
        talukaEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        talukaEditText.setTextColor(getResources().getColor(R.color.black));

        pinCodeEditText.setEnabled(false);
        pinCodeEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        pinCodeEditText.setTextColor(getResources().getColor(R.color.black));

        landmarkEditText.setEnabled(false);
        landmarkEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        landmarkEditText.setTextColor(getResources().getColor(R.color.black));

        propertyValueEditText.setEnabled(false);
        propertyValueEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        propertyValueEditText.setTextColor(getResources().getColor(R.color.black));
    }

    private void clearAndEnableFields() {
        propertyOwnerEditText.setText("");
        propertyOwnerEditText.setEnabled(true);
        propertyOwnerEditText.setBackgroundResource(R.drawable.edit_text_border);
        propertyOwnerEditText.setTextColor(getResources().getColor(R.color.black));

        propertyCategoryEditText.setText("");
        propertyCategoryEditText.setEnabled(true);
        propertyCategoryEditText.setBackgroundResource(R.drawable.edit_text_border);
        propertyCategoryEditText.setTextColor(getResources().getColor(R.color.black));

        typesOfCategoryEditText.setText("");
        typesOfCategoryEditText.setEnabled(true);
        typesOfCategoryEditText.setBackgroundResource(R.drawable.edit_text_border);
        typesOfCategoryEditText.setTextColor(getResources().getColor(R.color.black));

        plotNumberEditText.setText("");
        plotNumberEditText.setEnabled(true);
        plotNumberEditText.setBackgroundResource(R.drawable.edit_text_border);
        plotNumberEditText.setTextColor(getResources().getColor(R.color.black));

        localityEditText.setText("");
        localityEditText.setEnabled(true);
        localityEditText.setBackgroundResource(R.drawable.edit_text_border);
        localityEditText.setTextColor(getResources().getColor(R.color.black));

        villageEditText.setText("");
        villageEditText.setEnabled(true);
        villageEditText.setBackgroundResource(R.drawable.edit_text_border);
        villageEditText.setTextColor(getResources().getColor(R.color.black));

        stateEditText.setText("");
        stateEditText.setEnabled(true);
        stateEditText.setBackgroundResource(R.drawable.edit_text_border);
        stateEditText.setTextColor(getResources().getColor(R.color.black));

        districtEditText.setText("");
        districtEditText.setEnabled(true);
        districtEditText.setBackgroundResource(R.drawable.edit_text_border);
        districtEditText.setTextColor(getResources().getColor(R.color.black));

        cityEditText.setText("");
        cityEditText.setEnabled(true);
        cityEditText.setBackgroundResource(R.drawable.edit_text_border);
        cityEditText.setTextColor(getResources().getColor(R.color.black));

        talukaEditText.setText("");
        talukaEditText.setEnabled(true);
        talukaEditText.setBackgroundResource(R.drawable.edit_text_border);
        talukaEditText.setTextColor(getResources().getColor(R.color.black));

        pinCodeEditText.setText("");
        pinCodeEditText.setEnabled(true);
        pinCodeEditText.setBackgroundResource(R.drawable.edit_text_border);
        pinCodeEditText.setTextColor(getResources().getColor(R.color.black));

        landmarkEditText.setText("");
        landmarkEditText.setEnabled(true);
        landmarkEditText.setBackgroundResource(R.drawable.edit_text_border);
        landmarkEditText.setTextColor(getResources().getColor(R.color.black));

        propertyValueEditText.setText("");
        propertyValueEditText.setEnabled(true);
        propertyValueEditText.setBackgroundResource(R.drawable.edit_text_border);
        propertyValueEditText.setTextColor(getResources().getColor(R.color.black));
    }
}

