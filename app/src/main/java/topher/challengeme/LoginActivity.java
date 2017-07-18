package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Christopher Hurt on 7/18/2017.
 * This activity will allow users to login.
 */

public class LoginActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginUser(View view) {
        //Button is clicked and user is logged in.
    }
}
