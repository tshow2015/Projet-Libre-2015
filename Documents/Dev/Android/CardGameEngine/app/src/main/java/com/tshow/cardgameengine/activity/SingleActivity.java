package com.tshow.cardgameengine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tshow.R;
import com.tshow.cardgames.american8.American8OffActivity;
import com.tshow.cardgames.djambo.DjamboOffActivity;


public class SingleActivity extends Activity {

    private static final String TAG = "EngineActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String gamePlayed="";
        if (extras != null) {
            Log.d(TAG, "SA: onCreate(): extras != NULL");
            gamePlayed = extras.getString("gamePlayed");
            Log.d(TAG, "SA: onCreate():" +  gamePlayed);
        }
        else             Log.d(TAG, "IA: onCreate(): extras EQUALS NULL");

        Intent iT;
        switch (gamePlayed) {
            case "American8":
                Log.d(TAG, "");
                iT = new Intent(SingleActivity.this, American8OffActivity.class);
                startActivity(iT);
                finish();
                break;
            case "Djambo":
                Log.d(TAG, "SA: Djambo");
                iT = new Intent(SingleActivity.this, DjamboOffActivity.class);
                startActivity(iT);
                finish();
                break;
            default:
                Log.d(TAG, "SA: DEFAULT");
                finish();
        }
    }
}
