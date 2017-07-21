package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Christopher Hurt on 7/18/2017.
 * This activity will allow users to login.
 * Takes them to the MainActivity once logged in.
 */

public class LoginActivity extends Activity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputEmail;
    private EditText inputPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.loginEmailInput);
        inputPassword = (EditText) findViewById(R.id.loginPasswordInput);

        mAuth = FirebaseAuth.getInstance();
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginUser(View view) {
        //Button is clicked and user is logged in.
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if(!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            changeScreen(task.isSuccessful());
                            if(!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void changeScreen(boolean successful) {
        if(successful) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
