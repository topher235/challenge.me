package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WeekChallengeActivity extends Activity {
    public static final String TAG = WeekChallengeActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private ImageButton imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_challenge);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    setStar();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //Set star image button
        imgBtn = (ImageButton) findViewById(R.id.weeklyChallengeStarButton);
        setStar(); //Read if completed from db

        //Set star image button onClick event
        imgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(imgBtn.getTag().equals(R.drawable.star_off)) {           //can't call get image resource so getTag is called
                    imgBtn.setImageResource(R.drawable.star_on);
                    imgBtn.setTag(R.drawable.star_on);
                    updateUserPoints();
                }
            }
        });
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


    private void updateUserPoints(int points) {
        points += 5;
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").setValue(points);
        Log.d(TAG, "Value is: " + points);
        String tier = updateUserTier(points);
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("tier").setValue(tier);
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("weekly_challenge").setValue("complete");
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

    private void setStar() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("weekly_challenge").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get points and use the values to update the user
                        String s = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + s);
                        if(s.equals("complete")) {
                            setStarToOn();
                        } else {
                            setStarToOff();
                        }
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

    public void setStarToOn() {
        imgBtn.setImageResource(R.drawable.star_on);
        imgBtn.setTag(R.drawable.star_on);
    }

    public void setStarToOff() {
        imgBtn.setImageResource(R.drawable.star_off);
        imgBtn.setTag(R.drawable.star_off);
    }
}
