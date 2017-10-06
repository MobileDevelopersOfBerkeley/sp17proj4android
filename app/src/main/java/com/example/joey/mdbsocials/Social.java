package com.example.joey.mdbsocials;

/**
 * Created by joey on 9/28/17.
 */

public class Social  {
    String id;
    String name;
    String owner;
    String date;
    String description;
    int interested = 0;

    public Social(){}



    public Social(String id, String owner, String name, String date, String description, int interested) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.date = date;
        this.description = description;
        this.interested = interested;
    }

    public Social(String id, String owner, String name, String date, String description) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.date = date;
        this.description = description;
        this.interested = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInterested() {
        return interested;
    }

    public void setInterested(int interested) {
        this.interested = interested;
    }

}