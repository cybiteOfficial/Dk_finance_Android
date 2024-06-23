package com.example.bankapp;

import java.io.Serializable;

public class CustomerData implements Serializable {


    private String role;
    private String applicationId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String age;
    private String gender;
    private String title;
    private String customerSegment;
    private String industry;
    private String occupation;
    private String sourceOfIncome;
    private String income;
    private String familyIncome;
    private String numberOfDependents;
    private String residenceOwner;
    private String agricultureLandOwner;

    private String educationQualification;

    private String imagePath;

    // Add constructor, getters, and setters

    public CustomerData(String role, String applicationId, String firstName, String middleName, String lastName, String dob, String age, String gender, String title, String customerSegment, String numberOfDependents, String sourceOfIncome, String income, String familyIncome, String residenceOwner, String agricultureLandOwner, String educationQualification, String imagePath) {
        this.role = role;
        this.applicationId = applicationId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dob = dob;
        this.age = age;
        this.gender = gender;
        this.title = title;
        this.customerSegment = customerSegment;
        this.sourceOfIncome = sourceOfIncome;
        this.income = income;
        this.familyIncome = familyIncome;
        this.residenceOwner = residenceOwner;
        this.agricultureLandOwner = agricultureLandOwner;
        this.imagePath = imagePath;
    }

    // Getters and Setters

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomerSegment() {
        return customerSegment;
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }

//    public String getIndustry() {
//        return industry;
//    }
//
//    public void setIndustry(String industry) {
//        this.industry = industry;
//    }
//
//    public String getOccupation() {
//        return occupation;
//    }
//
//    public void setOccupation(String occupation) {
//        this.occupation = occupation;
//    }

    public String getNumberOfDependents() {
        return numberOfDependents;
    }

    public void setNumberOfDependents(String numberOfDependents) {
        this.numberOfDependents = numberOfDependents;
    }

    public String getSourceOfIncome() {
        return sourceOfIncome;
    }

    public void setSourceOfIncome(String sourceOfIncome) {
        this.sourceOfIncome = sourceOfIncome;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getFamilyIncome() {
        return familyIncome;
    }

    public void setFamilyIncome(String familyIncome) {
        this.familyIncome = familyIncome;
    }

    public String getResidenceOwner() {
        return residenceOwner;
    }

    public void setResidenceOwner(String residenceOwner) {
        this.residenceOwner = residenceOwner;
    }

    public String getAgricultureLandOwner() {
        return agricultureLandOwner;
    }

    public void setAgricultureLandOwner(String agricultureLandOwner) {
        this.agricultureLandOwner = agricultureLandOwner;
    }

    public String getEducationQualification() {
        return educationQualification;
    }

    public void setEducationQualification(String educationQualification) {
        this.educationQualification = educationQualification;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    @Override
    public String toString() {
        String jsonTemplate = "{" +
                "\"application_id\": \"" + applicationId + "\"," +
                "\"role\": \"" + role + "\"," +
                "\"firstName\": \"" + firstName + "\"," +
                "\"middle_name\": \"" + middleName + "\"," +
                "\"lastName\": \"" + lastName + "\"," +
                "\"dateOfBirth\": \"" + dob + "\"," +
                "\"age\": \"" + age + "\"," +
                "\"gender\": \"" + gender.toLowerCase() + "\"," +
                "\"title\": \"" + title.toLowerCase() + "\"," +
                "\"customerSegment\": \"" + customerSegment + "\"," +
                "\"numberOfDependents\": \"" + numberOfDependents + "\"," +
                "\"sourceOfIncome\": \"" + sourceOfIncome + "\"," +
                "\"monthlyIncome\": \"" + income + "\"," +
                "\"monthlyFamilyIncome\": \"" + familyIncome + "\"," +
                "\"residenceOwnership\": \"" + residenceOwner + "\"," +
                "\"agriculturalLand\": \"" + agricultureLandOwner + "\"," +
                "\"educationQualification\": \"" + educationQualification + "\"," +
                "\"valueOfAgriculturalLand\": \"" + "\"," +
                "\"earningsFromAgriculturalLand\": \"" + "\"" +
                "}";

        return jsonTemplate;

    }

}
