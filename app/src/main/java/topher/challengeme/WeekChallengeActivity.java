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
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //Set image button click event
        imgBtn = (ImageButton) findViewById(R.id.weeklyChallengeStarButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(imgBtn.getDrawable().equals(R.drawable.star_off)) {
                    imgBtn.setImageResource(R.drawable.star_on);
                    updateUserPoints();
                }
            }
        });
    }

    private boolean starIsOff() {
       // imgBtn.getResources
        return true;
    }

    private void updateUserPoints() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get points and use the values to update the user
                        int points = dataSnapshot.getValue(Integer.class);
                        Log.d(TAG, "Value is: " + points);
                        updateUserPoints(points);
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
    }
}
