package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import helper.User;

/**
 * Created by Christopher Hurt on 7/11/17.
 * This is the main activity of Challenge.me
 * This activity displays the daily challenge
 * and a menu.
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
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
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
        mDatabase.child("Users").addValueEventListener(//child(mAuth.getCurrentUser().getUid()).child("points").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get points and use the values to update the user
                        User user = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "Value is: " + user.toString());
                        user.updateUserPoints();
                        updateUserInDatabase(user.points, user.day, user.tier);
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
    }
}