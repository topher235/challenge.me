package helper;

/**
 * Created by Christopher Hurt on 7/20/2017.
 */

public class User {
    public String email;
    public int points;

    public User() {

    }

    public User(String email) {
        this.email = email;
        this.points = 0;
    }

    public void updateUserPoints() {
        this.points += 1;
    }
}
