package com.example.bankapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        return rootView;
    }

    // Method to update UI with CurrentAddressData
    public void updateData(CurrentAddressData currentAddressData) {
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
        } else {
            Log.e("PermanentFragment", "CurrentAddressData object is null.");
        }
    }
}
