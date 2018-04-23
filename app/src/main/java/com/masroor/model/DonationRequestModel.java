package com.masroor.model;

import com.masroor.blooddonationapp.Strs;

public class DonationRequestModel {

    private AdminLocationModel request_location;
    private String blood_type,needed_by_date,message;
    private boolean urgent;

    public DonationRequestModel(AdminLocationModel request_location, String blood_type, String needed_by_date, String message, boolean urgent) {
        this.request_location = request_location;
        this.blood_type = blood_type;
        this.needed_by_date = needed_by_date;
        this.message = message;
        this.urgent = urgent;
    }

    @Override
    public String toString() {
        return request_location.toString()+","+
                Strs.REQUEST_BLOOD_TYPE+blood_type+","+
                Strs.REQUEST_NEEDED_BY_DATE+needed_by_date+","+
                Strs.REQUEST_MESSAGE+message+","+
                Strs.REQUEST_URGENT+urgent;
    }

    public DonationRequestModel(){

    }

    public AdminLocationModel getRequest_location() {
        return request_location;
    }

    public void setRequest_location(AdminLocationModel request_location) {
        this.request_location = request_location;
    }

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(String blood_type) {
        this.blood_type = blood_type;
    }

    public String getNeeded_by_date() {
        return needed_by_date;
    }

    public void setNeeded_by_date(String needed_by_date) {
        this.needed_by_date = needed_by_date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}
