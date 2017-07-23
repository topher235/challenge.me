package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

/**
 * Created by Christopher Hurt on 7/12/2017.
 * This activity displays the account information
 * for the user.
 */

public class AccountActivity extends Activity {

    private static final String TAG = AccountActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageButton imgBtn;
    private TextView tierLabel;
    private TextView pointsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        imgBtn = (ImageButton) findViewById(R.id.account_profile_picture);
        tierLabel = (TextView) findViewById(R.id.account_tier_label);
        pointsLabel = (TextView) findViewById(R.id.account_points_label);

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
                    login();
                }
                // ...
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setTierLabelText();
        setPointsLabelText();
        setImage();
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

    //**** LOGIN / LOGOUT FUNCTIONS ****//

    private void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    //**** MENU FUNCTIONS ****//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.menu_weekly_challenge:
                startActivity(new Intent(this, WeekChallengeActivity.class));
                return true;
            case R.id.menu_about:
                //Go to about activity
            default:
                return false;
        }
    }

    //**** SET UI FUNCTIONS ****//

    private void setTierLabelText() {
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("tier").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get points and use the values to update the user
                        String s = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + s);
                        setTierLabelText(s);
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

    private void setTierLabelText(String s) {
        this.tierLabel.setText(s);
    }

    private void setPointsLabelText() {
        Log.d(TAG, "VALUE IS: " + mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").toString());
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get points and use the values to update the user
                        Integer i = dataSnapshot.getValue(Integer.class);
                        Log.d(TAG, "Value is: " + i);
                        setPointsLabelText(i);
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

    private void setPointsLabelText(Integer i) {
        this.pointsLabel.setText("Exp: " + i);
    }

    private void setImage() {
        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("profile_avatar").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get points and use the values to update the user
                        String s = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + s);
                        setImage(s);
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

    private void setImage(String str) {
        int id;
        switch(str) {
            case "profile_beard_guy":
                id = R.drawable.beard_guy;
                break;
            case "profile_afro_girl":
                id = R.drawable.afro_girl;
                break;
            case "profile_business_guy":
                id = R.drawable.business_guy;
                break;
            case "profile_brunette_girl":
                id = R.drawable.brunette_girl;
                break;
            case "profile_glasses_guy":
                id = R.drawable.glasses_guy;
                break;
            case "profile_headphones_girl":
                id = R.drawable.headphones_girl;
                break;
            case "profile_moustache_guy":
                id = R.drawable.moustache_guy;
                break;
            case "profile_ponytail_girl":
                id = R.drawable.ponytail_girl;
                break;
            default:
                id = R.drawable.beard_guy;
        }
        this.imgBtn.setImageResource(id);
    }
}
