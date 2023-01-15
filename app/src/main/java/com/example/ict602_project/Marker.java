package com.example.ict602_project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Marker {

    @SerializedName("Hazard")
    @Expose
    private String hazard;

    @SerializedName("Time")
    @Expose
    private String time;

    @SerializedName("Latitude")
    @Expose
    private String latitude;

    @SerializedName("Longitude")
    @Expose
    private String longitude;

    @SerializedName("Reported By")
    @Expose
    private String reportedBy;

    @SerializedName("ReportID")
    @Expose
    private String reportID;

    @SerializedName("UserID")
    @Expose
    private String userID;

    @SerializedName("HazardID")
    @Expose
    private String hazardID;

    public String getHazardID(){ return hazardID; }

    public String getHazard() {
        return hazard;
    }

    public void setHazard(String hazard) {
        this.hazard = hazard;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReportID(){ return reportID; }

    public String getUserID(){ return userID; }

}
