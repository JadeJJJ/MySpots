package com.example.myspots;

import com.google.android.gms.maps.model.LatLng;

public class Landmarks {
    private String userId;
    private String landMarkName;
    private String landMarkAddress;
    private LatLng position;

    public Landmarks(String userId, String landMarkName, String landMarkAddress, LatLng position) {
        this.userId = userId;
        this.landMarkName = landMarkName;
        this.landMarkAddress = landMarkAddress;
        this.position = position;
    }

    public Landmarks() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
