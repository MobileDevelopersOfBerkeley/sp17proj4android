package com.example.radhika.mdbsocials;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Radhika on 2/24/17.
 */
public class Social implements Comparable<Social>{
    private String name;
    private String emailAddress;
    private int numInterested;
    private String imageUrl;
    private long timestamp;
    private String date;
    private String description;
    private List<User> interested;

    public Social () {

    }

    public Social(String name, String hostEmail, int numInterested, String image, long timeMade, String description, String date, List<User> interested) {
        this.name = name;
        this.emailAddress = hostEmail;
        this.numInterested = numInterested;
        this.imageUrl = image;
        this.timestamp = timeMade;
        this.date = date;
        this.description = description;
        this.interested = interested;
    }

    public String getName(){
        return name;
    }

    public String getEmailAddress(){
        return emailAddress;
    }

    public int getNumInterested(){
        return numInterested;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public String getDate() {
        return date;
    }

    public List<User> getInterested() {
        return interested;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setNumInterested(int numInterested) {
        this.numInterested = numInterested;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInterested(List<User> interested ) {
        this.interested = interested;
    }

    public int compareTo(Social other){
        if (this.timestamp > other.timestamp)
            return -1;
        else if (this.timestamp < other.timestamp)
            return 1;
        return 0;
    }
    public boolean equals (Social other) {
        return this.timestamp == other.timestamp;
    }
}
