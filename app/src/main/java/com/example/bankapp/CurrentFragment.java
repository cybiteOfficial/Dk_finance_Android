package com.example.bankapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CurrentFragment extends Fragment {

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

    private DataTransferListener dataTransferListener;

    public void setDataTransferListener(DataTransferListener listener) {
        this.dataTransferListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current, container, false);

        // Initialize EditText fields
        propertyOwnerEditText = rootView.findViewById(R.id.currentPropertyOwner);
        propertyCategoryEditText = rootView.findViewById(R.id.currentPropertyCategory);
        typesOfCategoryEditText = rootView.findViewById(R.id.currentTypesOfCategory);
        plotNumberEditText = rootView.findViewById(R.id.currentPlotNumber);
        localityEditText = rootView.findViewById(R.id.currentLocality);
        villageEditText = rootView.findViewById(R.id.currentVillage);
        stateEditText = rootView.findViewById(R.id.currentState);
        districtEditText = rootView.findViewById(R.id.currentDistrict);
        cityEditText = rootView.findViewById(R.id.currentCity);
        talukaEditText = rootView.findViewById(R.id.currentTaluka);
        pinCodeEditText = rootView.findViewById(R.id.currentPinCode);
        landmarkEditText = rootView.findViewById(R.id.currentLandmark);
        propertyValueEditText = rootView.findViewById(R.id.currentPropertyValue);

        // Assuming automatic transfer when PermanentFragment is selected or any other action occurs
        // Here, you can add listeners or any logic to trigger data transfer automatically
        // For demonstration, let's assume data transfer happens on some event like fragment selection or button click in PermanentFragment

        return rootView;
    }

    // Method to retrieve current address data and transfer it
    public void transferCurrentAddressData() {
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

        // Create CurrentAddressData object
        CurrentAddressData addressData = new CurrentAddressData(propertyOwner, propertyCategory, typesOfCategory, plotNumber, locality, village, state, district, city, taluka, pinCode, landmark, propertyValue);

        // Pass data to listener if listener is set
        if (dataTransferListener != null) {
            dataTransferListener.onDataTransfer(addressData);
        }
    }
}
