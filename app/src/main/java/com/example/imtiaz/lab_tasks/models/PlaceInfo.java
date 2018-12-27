package com.example.imtiaz.lab_tasks.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {

    private String name;
    private LatLng latLng;

    public PlaceInfo(String name, LatLng latLng) {
        this.name = name;

        this.latLng = latLng;
    }

    public PlaceInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}
