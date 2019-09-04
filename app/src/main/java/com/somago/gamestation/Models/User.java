package com.somago.gamestation.Models;

public class User {

    private String id;
    private String name;
    private String mobile;
    private String address;

    public User(){

    }

    public User(String id, String name, String mobile, String address) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }
}
