package io.github.sumukhshiv.mdbsocials;

import java.util.ArrayList;

/**
 * Created by sumukhshivakumar on 2/24/17.
 */

public class Profile {

    String fullName;
    String email;
    String profileImage;
    ArrayList<String> socialsAttending;

    public Profile(String fullName, String email, String profileImage, ArrayList<String> socialsAttending) {
        this.fullName = fullName;
        this.email = email;
        this.profileImage = profileImage;
        this.socialsAttending = socialsAttending;
    }
}
