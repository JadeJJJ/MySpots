package com.example.myspots;

import com.google.android.gms.maps.model.LatLng;

public class Landmarks {
    private int userId;
    private String landMarkName;
    private String landMarkAddress;
    private LatLng position;


    public Landmarks(int userId, String landMarkName, String landMarkAddress, LatLng position) {
        this.userId = userId;
        this.landMarkName = landMarkName;
        this.landMarkAddress = landMarkAddress;
        this.position = position;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLandMarkName() {
        return landMarkName;
    }

    public void setLandMarkName(String landMarkName) {
        this.landMarkName = landMarkName;
    }

    public String getLandMarkAddress() {
        return landMarkAddress;
    }

    public void setLandMarkAddress(String landMarkAddress) {
        this.landMarkAddress = landMarkAddress;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }
}
