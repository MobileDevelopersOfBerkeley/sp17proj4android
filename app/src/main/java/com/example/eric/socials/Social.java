package com.example.eric.socials;

import java.util.ArrayList;

/**
 * Created by eric on 2/19/17.
 */

public class Social {
    String eventName;
    String eventImage;
    String emailOfCreator;
    String description;
    int numRSVP;
    String date;
    ArrayList<String> usersInterested;

    public Social(String eventName, String eventImage, String emailOfCreator, String description, int numRSVP, String date, ArrayList<String>usersInterested){
        this.eventName = eventName;
        this.eventImage = eventImage;
        this.emailOfCreator = emailOfCreator;
        this.description = description;
        this.numRSVP = numRSVP;
        this.date = date;
        this.usersInterested = usersInterested;
    }
}
