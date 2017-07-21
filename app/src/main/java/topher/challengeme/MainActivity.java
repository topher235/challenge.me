package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import helper.User;

/**
 * Created by Christopher Hurt on 7/11/17.
 * This is the main activity of Challenge.me.
 * Opens the login activity, if a user is not
 * detected.
 * This activity displays the daily challenge
 * and a menu.
 * When the star image is clicked, the database
 * will be updated with the new user information.
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private ImageButton imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    login();//startActivity(new Intent(MainActivity.class, LoginActivity.class));
                }
                // ...
            }
        };

        imgBtn = (ImageButton) findViewById(R.id.dailyChallengeStarButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateUserPoints();
                if(imgBtn.getDrawable().equals(R.drawable.star_off)) {
                    imgBtn.setImageResource(R.drawable.star_on);
                    updateUserPoints();
                }
            }
        });
    }

    private void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_account:
                //change activity to account
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            case R.id.menu_weekly_challenge:
                startActivity(new Intent(this, WeekChallengeActivity.class));
                return true;
            case R.id.menu_settings:
                //change activity to settings
                return true;
            case R.id.menu_about:
                //Change activity to about
                return true;
            case R.id.menu_sign_out:
                mAuth.signOut();
                return true;
            default:
                return false;
        }
    }

    private void updateUserPoints() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get points and use the values to update the user
                        Integer i = dataSnapshot.getValue(Integer.class);
                        Log.d(TAG, "Value is: " + i);
                        updateUserPoints(i);
                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting points failed, log a message
                        Log.w(TAG, "updateUserPoints:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }

    private void updateUserInDatabase(int points, int day, String tier) {
        mDatabase.child("Users").child(user.getUid()).child("points").setValue(points);
        mDatabase.child("Users").child(user.getUid()).child("day").setValue(day);
        mDatabase.child("Users").child(user.getUid()).child("tier").setValue(tier);
    }

    private void updateUserPoints(int points) {
        points += 2;
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").setValue(points);
        Log.d(TAG, "Value is: " + points);
        String tier = updateUserTier(points);
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("tier").setValue(tier);
    }

    private String updateUserTier(int points) {
        if (points >= 19 && points <= 38) {
            return "Novice";
        } else if(points >= 39 && points <= 76) {
            return "Bold";
        } else if(points >= 77 && points <= 133) {
            return "Opportunistic";
        } else if(points >= 134 && points <= 209) {
            return "Ambitious";
        } else if(points >= 210 && points <= 304) {
            return "Go-getter";
        } else if(points >= 305 && points <= 418) {
            return "Achiever";
        } else if(points >= 419 && points <= 551) {
            return "Adventurer";
        } else if(points >= 552 && points <= 703) {
            return "High-flyer";
        } else if(points >= 704 && points <= 874) {
            return "Over-Achiever";
        } else if(points >= 875 && points <= 1064) {
            return "Master";
        } else if(points >= 1065) {
            return "Champion";
        } else {
            return "Beginner";
        }
    }
}