package com.masroor.model;


import com.masroor.blooddonationapp.Strs;

public class AdminLocationModel {
    private double location_longitude,location_latitude;
    private String location_name;

    public AdminLocationModel(){

    }

    public AdminLocationModel(double location_longitude, double location_latitude, String location_name) {
        this.location_longitude = location_longitude;
        this.location_latitude = location_latitude;
        this.location_name = location_name;
    }

    @Override
    public String toString() {
        return Strs.ADMIN_LOCATION_NAME+location_name+","+
                Strs.ADMIN_LOCATION_LONGITUDE+location_longitude+","+
                Strs.ADMIN_LOCATION_LATITUDE+location_latitude;
    }

    public double getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(double location_longitude) {
        this.location_longitude = location_longitude;
    }

    public double getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(double location_latitude) {
        this.location_latitude = location_latitude;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }
}
