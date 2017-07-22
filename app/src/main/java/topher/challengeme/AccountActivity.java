package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        setTierLabelText();
        setPointsLabelText();

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

    private void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void getProfileImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imgBtn.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
        saveImageToDatabase();
    }

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

    private void setTierLabelText() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    private void setPointsLabelText() {

    }

    private void saveImageToDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}
