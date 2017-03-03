package com.example.datarsd1.mdbsocials_2;

import java.util.ArrayList;

/**
 * Created by Adhiraj Datar on 3/2/2017.
 */

public class Social {

    String title, host, description, date, firebaseLocation, id, hostuid;
    ArrayList<String> interested, interestedUIDs;
    Integer numInterested;

    public Social(String t, String h, String desc, String d, String fbl, String id, Integer numInterested, String hostuid, ArrayList<String> interested, ArrayList<String> interestedUIDs) {
        this.title = t;
        this.host = h;
        this.description = desc;
        this.date = d;
        this.firebaseLocation = fbl;
        this.id = id;
        this.interested = interested;
        this.interestedUIDs = interestedUIDs;
        this.numInterested = numInterested;
        this.hostuid = hostuid;
    }

}
