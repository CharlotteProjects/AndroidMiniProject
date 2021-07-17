package com.charlotteprojects.androidminiproject;

public class User {
    public String userName, userEmail, shopName, latitude, longitude;

    public User() {

    }

    public User(String name, String email, String shop){
        userName = name;
        userEmail = email;
        shopName = shop;
        latitude = "";
        longitude = "";
    }

    public void SetAddress(String addLatitude, String addLongitude){
        latitude = addLatitude;
        longitude = addLongitude;
    }
}
