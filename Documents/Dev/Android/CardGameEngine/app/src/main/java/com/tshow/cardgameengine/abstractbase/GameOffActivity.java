package com.tshow.cardgameengine.abstractbase;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.tshow.cardgameengine.cardelement.Card;
import com.tshow.cardgameengine.cardelement.Hand;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameOffActivity extends Activity
{
    protected TextView gameInfo;
    protected String winnerGame;
    protected String myParticipantId;
    protected String currentParticipantId;
    protected ArrayList<String> participantIds;
    
    public abstract void onPlayClicked(View view);
    public abstract void onFinishClicked(View view);
    protected abstract void whenDone();
    protected abstract void whenFinish();
    protected abstract void foreplay(Activity context, HashMap<String, Integer> CardReferenceMap);
    protected abstract void displayInfo();
    protected abstract void displayHand(final Activity context, final HashMap<String, Integer> CardReferenceMap, TextView t);
    protected abstract void currentPlay(Activity context, HashMap<String, Integer> CardReferenceMap,  Card chosenCard);
    protected abstract String getNextParticipantId();
    protected abstract void showSpinner();
    protected abstract void showWarning(String title, String message);

}
