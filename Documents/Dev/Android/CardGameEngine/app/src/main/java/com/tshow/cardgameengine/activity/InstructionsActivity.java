package com.tshow.cardgameengine.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tshow.R;


public class InstructionsActivity extends Activity {

    private static final String TAG = "EngineActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String gamePlayed="";
        if (extras != null) {
            Log.d(TAG, "IA: onCreate(): extras != NULL");
            gamePlayed = extras.getString("gamePlayed");
            Log.d(TAG, "IA: onCreate():" +  gamePlayed);
        }
        else             Log.d(TAG, "IA: onCreate(): extras EQUALS NULL");

        switch (gamePlayed) {
            case "American8":
                Log.d(TAG, "");
                setContentView(R.layout.activity_american8);
                findViewById(R.id.gameplay_layout).setVisibility(View.GONE);
                findViewById(R.id.instructions).setVisibility(View.VISIBLE);
                break;
            case "Djambo":
                Log.d(TAG, "IA: Djambo");
                setContentView(R.layout.activity_djambo);
                findViewById(R.id.gameplay_layout).setVisibility(View.GONE);
                findViewById(R.id.instructions).setVisibility(View.VISIBLE);
                break;
            default:
                Log.d(TAG, "");
        }
    }
}
