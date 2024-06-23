package com.example.bankapp;

public class CurrentAddressData {

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String state;
    private String district;
    private String city;
    private String taluka;
    private String pinCode;
    private String landmark;
    private String stabilityAtResidence;
    private String distanceFromBranch;
    private String residenceState;
    private String residenceType;

    public CurrentAddressData( String addressLine1 , String addressLine2, String addressLine3, String state, String district, String city, String taluka, String pinCode, String landmark, String stabilityAtResidence, String distanceFromBranch, String residenceState, String residenceType) {


        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.state = state;
        this.district = district;
        this.city = city;
        this.taluka = taluka;
        this.pinCode = pinCode;
        this.landmark = landmark;
        this.stabilityAtResidence = stabilityAtResidence;
        this.distanceFromBranch = distanceFromBranch;
        this.residenceState = residenceState;
        this.residenceType = residenceType;

    }


    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
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

    public String getStabilityAtResidence() {
        return stabilityAtResidence;
    }

    public String getDistanceFromBranch() {
        return distanceFromBranch;
    }

    public String getResidenceState() {
        return residenceState;
    }

    public String getResidenceType() {
        return residenceType;
    }

}
