package topher.challengeme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class WeekChallengeActivity extends AppCompatActivity {

    ImageButton imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_challenge);
        Intent intent = getIntent();

        //Set image button click event
        imgBtn = (ImageButton) findViewById(R.id.imageButton2);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imgBtn.setImageResource(R.drawable.star_on);
            }
        });
    }
}
