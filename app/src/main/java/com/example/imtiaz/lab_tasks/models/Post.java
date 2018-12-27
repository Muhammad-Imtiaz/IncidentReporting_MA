package com.example.imtiaz.lab_tasks.models;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.Principal;

public class Post {

    private String thumbnail_image;
    private String name;
    private String date;
    private String incidentCategory;
    private String incidentDescription;
    private String incident_image;
    private double lat;
    private double lng;
    private String locationTitle;

    public Post() {

    }

    public Post(String thumbnail_image, String name, String date, String incidentCategory,
                String incidentDescription, String incident_image, double lat, double lng, String  locationTitle) {
        this.thumbnail_image = thumbnail_image;
        this.name = name;
        this.date = date;
        this.incidentCategory = incidentCategory;
        this.incidentDescription = incidentDescription;
        this.incident_image = incident_image;
        this.lat = lat;
        this.lng = lng;
        this.locationTitle = locationTitle;
    }


    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIncidentCategory() {
        return incidentCategory;
    }

    public void setIncidentCategory(String incidentCategory) {
        this.incidentCategory = incidentCategory;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public String getIncident_image() {
        return incident_image;
    }

    public void setIncident_image(String incident_image) {
        this.incident_image = incident_image;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String  getLocationTitle() {
        return locationTitle;
    }

    public void setLocationTitle(String  locationTitle) {
        this.locationTitle = locationTitle;
    }

    @Override
    public String toString() {
        return "Post{" +
                "thumbnail_image='" + thumbnail_image + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", incidentCategory='" + incidentCategory + '\'' +
                ", incidentDescription='" + incidentDescription + '\'' +
                ", incident_image='" + incident_image + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", locationTitle=" + locationTitle +
                '}';
    }
}

