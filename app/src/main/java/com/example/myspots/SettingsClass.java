package com.example.myspots;

public class SettingsClass {
    private String userID;
    private String unitType;
    private String mapMode;
    private String landmarkType;

    public SettingsClass() {
    }

    public SettingsClass(String userID, String unitType, String mapMode, String landmarkType) {
        this.userID = userID;
        this.unitType = unitType;
        this.mapMode = mapMode;
        this.landmarkType = landmarkType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getMapMode() {
        return mapMode;
    }

    public void setMapMode(String mapMode) {
        this.mapMode = mapMode;
    }

    public String getLandmarkType() {
        return landmarkType;
    }

    public void setLandmarkType(String landmarkType) {
        this.landmarkType = landmarkType;
    }

}
