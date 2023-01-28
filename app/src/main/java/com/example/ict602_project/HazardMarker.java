package com.example.ict602_project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class HazardMarker {

    @SerializedName("hazardID")
    @Expose
    private String hazardID;
    @SerializedName("hazardname")
    @Expose
    private String hazardname;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("reportedBy")
    @Expose
    private String reportedBy;

    public String getHazardID() {
        return hazardID;
    }

    public void setHazardID(String hazardID) {
        this.hazardID = hazardID;
    }

    public String getHazardname() {
        return hazardname;
    }

    public void setHazardname(String hazardname) {
        this.hazardname = hazardname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

}