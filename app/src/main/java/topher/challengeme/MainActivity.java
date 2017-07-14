package topher.challengeme;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Christopher Hurt on 7/11/17.
 * This is the main activity of Challenge.me
 * This activity displays the daily challenge
 * and a menu.
 */
public class MainActivity extends AppCompatActivity {

    ImageButton imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set image button click event
        imgBtn = (ImageButton) findViewById(R.id.imageButton3);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imgBtn.setImageResource(R.drawable.star_on);
            }
        });
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
                return true;
            case R.id.menu_weekly_challenge:
                Intent intent = new Intent(this, WeekChallengeActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                //change activity to settings
                return true;
            case R.id.menu_about:
                //Change activity to about
                return true;
            default:
                return false;
        }
    }

}
