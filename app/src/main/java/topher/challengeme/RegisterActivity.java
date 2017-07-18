package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Christopher Hurt on 7/18/2017.
 * This Activity will allow users to register.
 */

public class RegisterActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void registerUser(View view) {
        //Button is clicked and user will be registered
    }
}
