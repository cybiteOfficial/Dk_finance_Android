package com.example.bankapp;

public class CurrentAddressData {
    private String propertyOwner;
    private String propertyCategory;
    private String typesOfCategory;
    private String plotNumber;
    private String locality;
    private String village;
    private String state;
    private String district;
    private String city;
    private String taluka;
    private String pinCode;
    private String landmark;
    private String propertyValue;

    public CurrentAddressData(String propertyOwner, String propertyCategory, String typesOfCategory, String plotNumber, String locality, String village, String state, String district, String city, String taluka, String pinCode, String landmark, String propertyValue) {
        this.propertyOwner = propertyOwner;
        this.propertyCategory = propertyCategory;
        this.typesOfCategory = typesOfCategory;
        this.plotNumber = plotNumber;
        this.locality = locality;
        this.village = village;
        this.state = state;
        this.district = district;
        this.city = city;
        this.taluka = taluka;
        this.pinCode = pinCode;
        this.landmark = landmark;
        this.propertyValue = propertyValue;
    }

    public String getPropertyOwner() {
        return propertyOwner;
    }

    public String getPropertyCategory() {
        return propertyCategory;
    }

    public String getTypesOfCategory() {
        return typesOfCategory;
    }

    public String getPlotNumber() {
        return plotNumber;
    }

    public String getLocality() {
        return locality;
    }

    public String getVillage() {
        return village;
    }

    public String getState() {
        return state;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public String getTaluka() {
        return taluka;
    }

    public String getPinCode() {
        return pinCode;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}
