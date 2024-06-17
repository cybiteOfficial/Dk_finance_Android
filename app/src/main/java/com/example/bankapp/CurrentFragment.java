package com.example.bankapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CurrentFragment extends Fragment {

    private EditText propertyOwnerEditText;
    private EditText propertyCategoryEditText;
    private EditText typesOfCategoryEditText;
    private EditText occupationStatusEditText;
    private EditText propertyTitleEditText;
    private EditText houseNumberEditText;
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

    private DataTransferListener dataTransferListener;

    public void setDataTransferListener(DataTransferListener listener) {
        this.dataTransferListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current, container, false);

        // Initialize EditText views
        propertyOwnerEditText = view.findViewById(R.id.currentPropertyOwner);
        propertyCategoryEditText = view.findViewById(R.id.currentPropertyCategory);
        typesOfCategoryEditText = view.findViewById(R.id.currentTypesOfCategory);
        occupationStatusEditText = view.findViewById(R.id.occupationStatus);
        propertyTitleEditText = view.findViewById(R.id.propertyTitle);
        houseNumberEditText = view.findViewById(R.id.flatNumber);
        plotNumberEditText = view.findViewById(R.id.currentPlotNumber);
        localityEditText = view.findViewById(R.id.currentLocality);
        villageEditText = view.findViewById(R.id.currentVillage);
        stateEditText = view.findViewById(R.id.currentState);
        districtEditText = view.findViewById(R.id.currentDistrict);
        cityEditText = view.findViewById(R.id.currentCity);
        talukaEditText = view.findViewById(R.id.currentTaluka);
        pinCodeEditText = view.findViewById(R.id.currentPinCode);
        landmarkEditText = view.findViewById(R.id.currentLandmark);
        propertyValueEditText = view.findViewById(R.id.currentPropertyValue);

        // Set click listener for the button
        Button nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToPermanentFragment();
                if (dataTransferListener != null) {
                    dataTransferListener.switchFragment();
                }
            }
        });

        return view;
    }

    private void sendDataToPermanentFragment() {
        if (dataTransferListener != null) {
            String propertyOwner = propertyOwnerEditText.getText().toString();
            String propertyCategory = propertyCategoryEditText.getText().toString();
            String typesOfCategory = typesOfCategoryEditText.getText().toString();
            String occupationStatus = occupationStatusEditText.getText().toString();
            String propertyTitle = propertyTitleEditText.getText().toString();
            String houseNumber = houseNumberEditText.getText().toString();
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

            CurrentAddressData data = new CurrentAddressData(propertyOwner, propertyCategory, typesOfCategory, occupationStatus, propertyTitle, houseNumber, plotNumber, locality, village, state, district, city, taluka, pinCode, landmark, propertyValue);
            dataTransferListener.onDataTransfer(data);
        }
    }
}
