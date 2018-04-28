package com.masroor.blooddonationapp.model;

public class DonorModel {
    private String donor_uid,donor_name,donor_blood_type,donor_city;

    DonorModel(){

    }

    public DonorModel(String donor_uid, String donor_name, String donor_blood_type, String donor_city) {
        this.donor_uid = donor_uid;
        this.donor_name = donor_name;
        this.donor_blood_type = donor_blood_type;
        this.donor_city = donor_city;
    }

    public String getDonor_uid() {
        return donor_uid;
    }

    public void setDonor_uid(String donor_uid) {
        this.donor_uid = donor_uid;
    }

    public String getDonor_name() {
        return donor_name;
    }

    public void setDonor_name(String donor_name) {
        this.donor_name = donor_name;
    }

    public String getDonor_blood_type() {
        return donor_blood_type;
    }

    public void setDonor_blood_type(String donor_blood_type) {
        this.donor_blood_type = donor_blood_type;
    }

    public String getDonor_city() {
        return donor_city;
    }

    public void setDonor_city(String donor_city) {
        this.donor_city = donor_city;
    }
}
