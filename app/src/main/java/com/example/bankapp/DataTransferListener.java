package com.example.bankapp;

public interface DataTransferListener {
    void onDataTransfer(CurrentAddressData currentAddressData);
    void switchFragment();
}
