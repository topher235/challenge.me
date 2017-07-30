package topher.challengeme;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Christopher Hurt on 7/12/2017.
 * This activity displays information about
 * Challenge.me.
 */

public class AboutActivity extends Activity {
    private TextView message;
    private String donationMsg = " I have \n" +
            "made Challenge.Me free to use, but \n" +
            "if you are enjoying the challenges \n" +
            "and feel so inclined, I'd love you \n" +
            "if you bought me a beer or coffee \n" +
            "Thank you, you beautiful person!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Set the message on the About activity
        message = (TextView) findViewById(R.id.aboutMessage);
        setMessage();
    }

    /**
     * Sets the text of the About activity
     */
    private void setMessage() {
        message.setText("Challenge.Me is an app made for "
        + "the people who love to accept "
        + "challenges. Whether you "
        + "are practicing your over-achieving, "
        + "go-getter attitude or you just want "
        + "something to shake up life, "
        + "Challenge.Me will reward those who "
        + "stick with the challenges. You will have "
        + "100 days of challenges to climb"
        + "through the levels.");
    }
}
