package com.somago.gamestation.Models;

import java.io.Serializable;

public class Game implements Serializable {

    private String name;
    private String condition;
    private String console;
    private String image;
    private String userID;
    private String price;

    public Game() {
    }

    public Game(String name, String condition, String console, String image, String userID, String price) {
        this.name = name;
        this.condition = condition;
        this.console = console;
        this.image = image;
        this.userID = userID;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public String getConsole() {
        return console;
    }

    public String getImage() {
        return image;
    }

    public String getUserID() {
        return userID;
    }

    public String getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
