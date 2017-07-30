package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Christopher Hurt on 7/22/2017.
 * This activity will allow users to choose a
 * profile picture and write their choice into
 * a database.
 *
 * CREDIT: Avatars were created by Freepik
 */

public class ProfileImageActivity extends Activity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Called when an image is clicked. Sets
     * the file name to userChoice.
     * @param view
     */
    public void getImage(View view) {
        switch(view.getId()) {
            case R.id.profile_beard_guy:
                userChoice = "profile_beard_guy";
                break;
            case R.id.profile_afro_girl:
                userChoice = "profile_afro_girl";
                break;
            case R.id.profile_business_guy:
                userChoice = "profile_business_guy";
                break;
            case R.id.profile_brunette_girl:
                userChoice = "profile_brunette_girl";
                break;
            case R.id.profile_glasses_guy:
                userChoice = "profile_glasses_guy";
                break;
            case R.id.profile_headphones_girl:
                userChoice = "profile_headphones_girl";
                break;
            case R.id.profile_moustache_guy:
                userChoice = "profile_moustache_guy";
                break;
            case R.id.profile_ponytail_girl:
                userChoice = "profile_ponytail_girl";
                break;
            default:
                userChoice = "profile_beard_guy";
        }
        setImage();
    }

    /**
     * Puts the image string choice into the database.
     */
    private void setImage() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("profile_avatar").setValue(userChoice);
        changeScreen();
    }

    /**
     * Starts the MainActivity.
     */
    private void changeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
