package com.tshow.cardgameengine;

//import com.example.fdtest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.tshow.R;
import com.tshow.american8.American8Activity;
import com.tshow.djambo.DjamboActivity;


public class ChooseGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);

        ImageButton buttonAmerican8 = (ImageButton) findViewById(R.id.buttonAmerican8);
        buttonAmerican8.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //STUFF
                Intent i = new Intent(ChooseGameActivity.this, American8Activity.class);
                i.putExtra("gamePlayed", "American8");
                startActivity(i);

            }
        });

        ImageButton buttonDjambo = (ImageButton) findViewById(R.id.buttonDjambo);
        buttonDjambo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //STUFF
                Intent i = new Intent(ChooseGameActivity.this, DjamboActivity.class);
                i.putExtra("gamePlayed", "Djambo");
                startActivity(i);

            }
        });
    }
}
