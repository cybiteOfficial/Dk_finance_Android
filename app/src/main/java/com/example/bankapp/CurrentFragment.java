package com.example.bankapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CurrentFragment extends Fragment {

    private EditText addressLine1EditText;
    private EditText addressLine2EditText;
    private EditText addressLine3EditText;
    private EditText stateEditText;
    private EditText districtEditText;
    private EditText cityEditText;
    private EditText talukaEditText;
    private EditText pinCodeEditText;
    private EditText landmarkEditText;
    private EditText stabilityAtResidenceEditText;
    private EditText distanceFromBranchEditText;
    private Spinner residenceStateSpinner;
    private Spinner residenceTypeSpinner;

    private DataTransferListener dataTransferListener;

    public void setDataTransferListener(DataTransferListener listener) {
        this.dataTransferListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current, container, false);

        // Initialize EditText views
        addressLine1EditText = view.findViewById(R.id.curr_address_line_1);
        addressLine2EditText = view.findViewById(R.id.curr_address_line_2);
        addressLine3EditText = view.findViewById(R.id.curr_address_line_3);
        stateEditText = view.findViewById(R.id.currentState);
        districtEditText = view.findViewById(R.id.currentDistrict);
        cityEditText = view.findViewById(R.id.currentCity);
        talukaEditText = view.findViewById(R.id.currentTaluka);
        pinCodeEditText = view.findViewById(R.id.currentPinCode);
        landmarkEditText = view.findViewById(R.id.currentLandmark);
        stabilityAtResidenceEditText = view.findViewById(R.id.stabilityAtResidence);
        distanceFromBranchEditText = view.findViewById(R.id.distanceFromBranch);

        // Setup Spinners
        residenceStateSpinner = setupSpinner(view, R.id.residenceState, R.array.residence_state);
        residenceTypeSpinner = setupSpinner(view, R.id.residenceType, R.array.residence_type);

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

    private Spinner setupSpinner(View view, int spinnerId, int arrayId) {
        Spinner spinner = view.findViewById(spinnerId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.sample_spinner_item, getResources().getStringArray(arrayId)) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private void sendDataToPermanentFragment() {
        if (dataTransferListener != null) {
            String addressLine1 = addressLine1EditText.getText().toString();
            String addressLine2 = addressLine2EditText.getText().toString();
            String addressLine3 = addressLine3EditText.getText().toString();
            String state = stateEditText.getText().toString();
            String district = districtEditText.getText().toString();
            String city = cityEditText.getText().toString();
            String taluka = talukaEditText.getText().toString();
            String pinCode = pinCodeEditText.getText().toString();
            String landmark = landmarkEditText.getText().toString();
            String stabilityAtResidence = stabilityAtResidenceEditText.getText().toString();
            String distanceFromBranch = distanceFromBranchEditText.getText().toString();
            String residenceState = residenceStateSpinner.getSelectedItem().toString();
            String residenceType = residenceTypeSpinner.getSelectedItem().toString();

            CurrentAddressData data = new CurrentAddressData(addressLine1, addressLine2, addressLine3, state, district, city, taluka, pinCode, landmark, stabilityAtResidence, distanceFromBranch, residenceState, residenceType);
            dataTransferListener.onDataTransfer(data);
        }
    }
}
