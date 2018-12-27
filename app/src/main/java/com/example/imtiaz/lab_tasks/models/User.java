package com.example.imtiaz.lab_tasks.models;

public class User {

    String name, email, profile, status;

    public User() {
    }

    public User(String name, String email, String profile, String status) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.status = status;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profile='" + profile + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
