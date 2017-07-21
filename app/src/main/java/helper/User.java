package helper;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christopher Hurt on 7/20/2017.
 * User object containing data for email,
 * tier level, number of points, and the
 * day.
 */

public class User {
    public String email; //email of the current user
    public String tier; //name of the tier
    public int points;  //total number of points accrued by user
    public int day;     //the number used for determining the daily challenge

    public User() {

    }

    public User(String email) {
        this.email = email;
        this.tier = "Beginner";
        this.points = 0;
        this.day = 1;
    }



    public String toString() {
        return "Email: " + email + " | points: " + points + " | tier: " + tier;
    }
}
