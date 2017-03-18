package com.example.radhika.mdbsocials;

/**
 * Created by Radhika on 2/25/17.
 */

public class User {

    private String name;
    private String email;
    private String imageUrl;

    public User () {

    }
    public User(String name, String email, String imageUrl) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @Override
    public boolean equals(Object other) {
        return this.getName().equals(((User)other).getName());
    }
}
