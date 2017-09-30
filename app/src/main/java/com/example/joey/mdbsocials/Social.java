package com.example.joey.mdbsocials;

/**
 * Created by joey on 9/28/17.
 */
public class Social {
    String id;
    String name;
    String owner;
    String date;
    String description;
    int interested = 0;

    public Social(String id, String owner, String name, String date, String description, int interested) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.date = date;
        this.description = description;
        this.interested = interested;
    }

}