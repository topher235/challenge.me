package topher.challengeme;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private String[] challenges;
    private TextView challengeText;

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

        //Set the challenge
        challengeText = (TextView) findViewById(R.id.weeklyChallengeChallengeText);
        setChallenge();
    }

    // **** AUTH LISTENER METHODS **** //
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

    // **** UPDATE DATABASE METHODS **** //

    /**
     * Updates the user points in the database.
     */
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

    /**
     * Helper method for updateUserPoints. Sets
     * the new point value and updates the tier
     * label.
     * @param points - integer number of previous points of the user.
     */
    private void updateUserPoints(int points) {
        points += 5;
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").setValue(points);
        Log.d(TAG, "Value is: " + points);
        String tier = updateUserTier(points);
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("tier").setValue(tier);
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("weekly_challenge").setValue("complete");
    }

    /**
     * Returns the correct tier string based on the
     * number of points the user has.
     * @param points - integer number of points the user has.
     * @return String tier label
     */
    private String updateUserTier(int points) {
        if (points >= 6 && points <= 15) {
            return "Novice";
        } else if(points >= 16 && points <= 21) {
            return "Bold";
        } else if(points >= 22 && points <= 30) {
            return "Opportunistic";
        } else if(points >= 31 && points <= 42) {
            return "Ambitious";
        } else if(points >= 43 && points <= 57) {
            return "Go-getter";
        } else if(points >= 58 && points <= 75) {
            return "Achiever";
        } else if(points >= 76 && points <= 96) {
            return "Adventurer";
        } else if(points >= 97 && points <= 120) {
            return "High-flyer";
        } else if(points >= 121 && points <= 150) {
            return "Over-Achiever";
        } else if(points >= 151 && points <= 183) {
            return "Master";
        } else if(points >= 184) {
            return "Champion";
        } else {
            return "Beginner";
        }
    }

    /**
     * Sets the star based on if the current
     * challenge has been completed or not.
     */
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

    /**
     * Sets the star to gold (on).
     */
    public void setStarToOn() {
        imgBtn.setImageResource(R.drawable.star_on);
        imgBtn.setTag(R.drawable.star_on);
    }

    /**
     * Sets the star to transparent (off).
     */
    public void setStarToOff() {
        imgBtn.setImageResource(R.drawable.star_off);
        imgBtn.setTag(R.drawable.star_off);
    }

    /**
     * Sets the challenge based on the number of days
     * the user has been registered.
     */
    public void setChallenge() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("day").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = dataSnapshot.getValue(Integer.class);
                        setChallenge(i/7);
                        Log.v(TAG, "Value is: " + i);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.v(TAG, "setChallenge:onCanceled", databaseError.toException());
                    }
                }
        );
    }

    /**
     * Helper method for setChallenge.
     * @param i - integer number for determining the challenge.
     */
    private void setChallenge(int i) {
        challenges = new String[]{
                "Set an alarm and don't hit snooze.",
                "Make plans with a friend and go through with it.",
                "Try a new hobby.",
                "Begin to learn something new.",
                "Set a goal and achieve it.",
                "Create a budget and stick to it.",
                "Workout 3 days this week.",
                "Conduct a review of the past week. What did you accomplish? What went wrong? What went right?",
                "Keep a food log.",
                "Walk 10,000 steps every day this week.",
                "Try an activity that gets the adrenaline pumping.",
                "Volunteer somewhere this week.",
                "Remind yourself of something you want, and make progress for getting it.",
                "Avoid elevators and escalators; take the stairs instead."
        };
        try {
            this.challengeText.setText(challenges[i]);
        } catch(ArrayIndexOutOfBoundsException err) {
            this.challengeText.setText("There are no more challenges to be completed at this time.");
        }
    }
}
