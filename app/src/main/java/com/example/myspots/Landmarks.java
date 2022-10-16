package com.example.myspots;

import com.google.android.gms.maps.model.LatLng;

public class Landmarks {
    private String userId;
    private String landMarkName;
    private String landMarkAddress;
    private Double Latitude;
    private Double longitude;

    public Landmarks(String userId, String landMarkName, String landMarkAddress, Double latitude, Double longitude) {
        this.userId = userId;
        this.landMarkName = landMarkName;
        this.landMarkAddress = landMarkAddress;
        Latitude = latitude;
        this.longitude = longitude;
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

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
