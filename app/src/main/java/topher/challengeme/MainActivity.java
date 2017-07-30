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
import android.widget.TextView;

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
    private TextView todayChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set star image button
        imgBtn = (ImageButton) findViewById(R.id.dailyChallengeStarButton);

        //Set today's challenge
        todayChallenge = (TextView) findViewById(R.id.dailyChallenge);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    setStar();
                    setTodayChallenge();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    login();
                }
                // ...
            }
        };

        //Set star image onClick listener
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
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
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
        imgBtn.setImageResource(R.drawable.star_on);

    }

    private void updateUserPoints(int points) {
        points += 2;
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").setValue(points);
        Log.d(TAG, "Value is: " + points);
        String tier = updateUserTier(points);
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("tier").setValue(tier);
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("daily_challenge").setValue("complete");
    }

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

    private void setStar() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("daily_challenge").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get string that specifies if user has completed the challenge
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
                        // Getting string failed, log a message
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

    public void setTodayChallenge() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("day").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get the day and set the challenge text based on its array index
                        Integer i = dataSnapshot.getValue(Integer.class);
                        Log.d(TAG, "Value is: " + i);
                        setTodayChallenge(i);
                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting day failed, log a message
                        Log.w(TAG, "setTodayChallenge:onCancelled", databaseError.toException());
                        // ...
                    }
                });
    }

    private void setTodayChallenge(int i) {
        String[] challenges = new String[] {
                "Write down ten things you're thankful for.",
                "Jam out to your favorite song in public.",
                "Pick a bad habit and plan to gradually end it.",
                "Find someone that needs help and help them.",
                "Take a new route to a destination.",
                "Try a new coffee or tea today.",
                "Take a picture that captures a memory from today.",
                "Meditate for twenty minutes to reduce stress.",
                "Compliment a stranger today.",
                "Cut out some sugar today.",
                "Take a walk without a destination.",
                "Brush your teeth twice and floss.",
                "Say 'Good morning/afternoon' to someone.",
                "Organize your book or movie collection.",
                "Begin to grow a plant.",
                "Eat your lunch outside.",
                "Read a news article with views that oppose yours.",
                "Call a friend to catch up.",
                "Find a new cartoon that makes you laugh.",
                "Don't complain today.",
                "Reduce the amount of TV you watch today.",
                "Plan tomorrow, today.",
                "Eat 5 servings of vegetables today.",
                "Greet a neighbor you've never spoken to.",
                "Double your exercise today.",
                "Don't visit social media sites today.",
                "Think before speaking today.",
                "Write a letter to one of your idols.",
                "Wear something fancy today just because.",
                "Walk in the rain or lie in the sun. Just spend at least an hour outside.",
                "Confess one of your secrets to someone.",
                "Try to impress someone today.",
                "Grab a drink and spend time going through old photos.",
                "Eat 3 servings of fruit today.",
                "Remain focused while someone is talking to you.",
                "Pick up your favorite book and start reading it again.",
                "Only use cash to purchase things today.",
                "Have a wholesome breakfast today.",
                "Make eye contact with everyone you see today.",
                "Be early for everything today.",
                "Don't drink carbonated drinks today.",
                "Prepare a meal in bed for someone you know.",
                "Turn off your cell phone for the day.",
                "Set aside a bin at your home for recycling.",
                "Cook dinner with someone today.",
                "Let someone go ahead of you in traffic.",
                "Give yourself credit for something you achieved today.",
                "Keep your impulses in check and make sure every decision you make is rational.",
                "Try some food you think you don't like but have never actually tried.",
                "Check your closet for clothes that don't fit. Donate ones that don't.",
                "Give a gift to someone you love.",
                "Get someone to tell you a story about their life.",
                "Support the local animal shelter.",
                "Handwrite a letter to someone you talk to often.",
                "Help someone reach a goal they have.",
                "Draw a picture of something you love.",
                "Try your luck on a game of chance.",
                "Completely shut down your computer when you are not using it.",
                "Learn to play a new game.",
                "Learn to say thank you in another language. Teach someone else.",
                "Go on an impromptu adventure.",
                "Learn the history of the city you were born in.",
                "For every year you have been alive, write down the most important thing that happened to you.",
                "Focus on getting ahead with work today.",
                "Challenge someone to a contest.",
                "Surprise someone with something that will make them happy.",
                "Give a stranger a high-five.",
                "Set a new personal record in something you do.",
                "Serenade someone with a song.",
                "Find an online petition that you believe in and sign it.",
                "Engage a stranger in an intellectual discussion.",
                "Exercise your mind with a puzzle of riddle.",
                "Focus on a single task at a time, without multi-tasking.",
                "Support a local artist by purchasing their work or spreading their name.",
                "Treat yourself to dessert today.",
                "Retry something you were unable to do in the past.",
                "Dress to impress today.",
                "Note six great things about yourself.",
                "Inform yourself about a common disability you have heard about.",
                "Pick up litter you see on the street and throw it away.",
                "Push yourself to your physical limit during a workout.",
                "Reflect on how you've changed in your past.",
                "Plan a trip to somewhere you have never been.",
                "Go for a run today.",
                "Do something for your alma mater.",
                "Buy someone a cup of coffee.",
                "Give everyone you talk to a sincere compliment.",
                "Don't go online unless it is absolutely necessary.",
                "Visit a tourist attraction near your home.",
                "Clean up your computer, inside and out.",
                "Take a long walk somewhere where you can admire nature's beauty.",
                "Buy a new book to read.",
                "Find a picture of you as a child. Show it to your friends.",
                "Take twenty minutes out of your work day to stretch and get the blood flowing.",
                "Write a letter to your future self. Put it somewhere you don't check often.",
                "Go out with a good friend.",
                "Listen to music outside of your norm.",
                "Sit and watch the sun set.",
                "Go to sleep early tonight.",
                "Clean your workspace and bedroom.",
        };
        try {
            this.todayChallenge.setText(challenges[i]);
        } catch(ArrayIndexOutOfBoundsException err) {
            this.todayChallenge.setText("There are no more challenges to be completed at this time.");
        }
    }
}