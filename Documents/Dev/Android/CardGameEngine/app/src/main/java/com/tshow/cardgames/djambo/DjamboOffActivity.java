package com.tshow.cardgames.djambo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tshow.R;
import com.tshow.cardgameengine.cardelement.Card;
import com.tshow.cardgameengine.cardelement.Hand;
import com.tshow.cardgameengine.MyOnClickListener;
import com.tshow.cardgameengine.abstractbase.GameOffActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jordantankoua on 01/06/15.
 */
public class DjamboOffActivity extends GameOffActivity {
    private static final String TAG = "DjamboOffActivity";
    private DjamboTurn mTurnData;
    private boolean cardAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_djambo);
        findViewById(R.id.parameters_panel).setVisibility(View.VISIBLE);
    }
    public void onPlayClicked(View view)
    {
        final Spinner spinnerNPlayers = (Spinner) findViewById(R.id.spinner_nplayers);
        final Spinner spinnerNRounds = (Spinner) findViewById(R.id.spinner_nrounds);
        final Spinner spinnerNCards = (Spinner) findViewById(R.id.spinner_ncards);
        final RadioGroup radioGLevel = (RadioGroup) findViewById(R.id.radio_level);

        int nCPUParticipants = Integer.parseInt(spinnerNPlayers.getSelectedItem().toString())-1;
        int nRounds = Integer.parseInt(spinnerNRounds.getSelectedItem().toString());
        int nCards = Integer.parseInt(spinnerNCards.getSelectedItem().toString());
        int level = radioGLevel.getCheckedRadioButtonId();
        myParticipantId = "player";
        currentParticipantId = myParticipantId;
        participantIds = new ArrayList<>();
        participantIds.add(myParticipantId);
        for(int i=0; i< nCPUParticipants; i++)
        {
            participantIds.add("cpu"+i);
        }
        findViewById(R.id.parameters_panel).setVisibility(View.GONE);

        mTurnData = new DjamboTurn(participantIds, nRounds, nCards, level);
        foreplay(this, DjamboTurn.CardReferenceMap);
    }
    public void onFinishClicked(View view) {
        Intent iT = new Intent(this, DjamboActivity.class);
        startActivity(iT);
        finish(); // Call once you redirect to another activity
    }
    protected void whenDone() {
        Log.d(TAG, "==== whenDone ");
        mTurnData.turn++;
        showSpinner();
        currentParticipantId = getNextParticipantId();
        showSpinner();
        foreplay(this, DjamboTurn.CardReferenceMap);
    }

    protected void whenFinish() {
        showSpinner();
        Log.d(TAG, "whenFN");

        setContentView(R.layout.activity_djambo);
        if(winnerGame.equals(myParticipantId))
            showWarning("YES", "YOU'VE WON THE GAME");
        else showWarning("SORRY", "YOU LOST.\nPlayer "+winnerGame+" WON.");
    }

    protected void foreplay(Activity context, HashMap<String, Integer> CardReferenceMap)
    {

        Log.d(TAG, "==== Round: " + mTurnData.round);
        Log.d(TAG, "==== Turn: " + mTurnData.turn);
        Log.d(TAG, "==== Id: " + currentParticipantId);
        if (mTurnData == null) Log.d(TAG, "*** Data null");

        gameInfo = (TextView) context.findViewById(R.id.gameInfo);
        Log.d(TAG, "*** ZONE");

        LinearLayout tableLayout = (LinearLayout) context.findViewById(R.id.tableLayout);
        if(tableLayout.getChildCount() > 0)
            tableLayout.removeAllViews();

        HorizontalScrollView hScrollView = new HorizontalScrollView(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        hScrollView.addView(linearLayout);
        tableLayout.addView(hScrollView);
        for(int i=0; i<mTurnData.plays.get(myParticipantId).getCardCount();i++)
        {
            String ResourceString = mTurnData.plays.get(myParticipantId).getCard(i).getResourceString();
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(CardReferenceMap.get(ResourceString));
            linearLayout.addView(imageView);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 10);
            imageView.setLayoutParams(lp);
            final float scale = context.getResources().getDisplayMetrics().density;
            int height_dps = 100;
            int width_dps = 80;
            int height_pixels = (int) (height_dps * scale + 0.5f);
            int width_pixels = (int) (width_dps * scale + 0.5f);
            imageView.getLayoutParams().height=height_pixels;
            imageView.getLayoutParams().width=width_pixels;
        }
        for (HashMap.Entry<String, Hand> entry : mTurnData.plays.entrySet())
        {
            if(!myParticipantId.equals(entry.getKey()))
            {
                HorizontalScrollView hScrollView2 = new HorizontalScrollView(context);
                LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                hScrollView2.addView(linearLayout2);
                tableLayout.addView(hScrollView2);
                for (int i = 0; i < entry.getValue().getCardCount(); i++)
                {
                    String ResourceString = entry.getValue().getCard(i).getResourceString();
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(CardReferenceMap.get(ResourceString));
                    linearLayout2.addView(imageView);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 10, 10);
                    final float scale = context.getResources().getDisplayMetrics().density;
                    int height_dps = 100;
                    int width_dps = 80;
                    int height_pixels = (int) (height_dps * scale + 0.5f);
                    int width_pixels = (int) (width_dps * scale + 0.5f);imageView.setLayoutParams(lp);imageView.getLayoutParams().height=height_pixels;
                    imageView.getLayoutParams().width=width_pixels;
                }
            }
        }

        //displayInfo(mTurnData, playerHand);
        displayInfo();

        if(mTurnData.turn > mTurnData.finalTurn)//ENDGAME
        {
            Log.d(TAG, "*** ZONE: FINISH");
            int maxRounds=0;
            for (HashMap.Entry<String, Integer> entry : mTurnData.roundsWon.entrySet())
            {
                if (entry.getValue()>maxRounds)
                {
                    maxRounds = entry.getValue();
                    winnerGame = mTurnData.nInitiativeId;
                }
            }
            Log.d(TAG, "*** currP:" + currentParticipantId);
            Log.d(TAG, "*** wG:"  + winnerGame);
            if(currentParticipantId.equals(winnerGame))
                whenFinish();
            else
                whenFinish();
            return;
        }
        else Log.d(TAG, "*** not FINISH ");
        displayHand(context, CardReferenceMap, null);
    }
    protected void displayInfo()
    {
        String stRoundsWon="Rounds:: ";
        for (HashMap.Entry<String, Integer> entry : mTurnData.roundsWon.entrySet())
        {
            stRoundsWon +=entry.getKey()+": "+(entry.getValue())+"  ";
        }
        stRoundsWon += "\n";
        gameInfo.setText(
                "Round: " + (mTurnData.round) + "  " +
                        "Turn: " + (mTurnData.turn) + "  " +
                        "Initiative: " + (mTurnData.nInitiativeId) + "\n" +
                        stRoundsWon
        );
    }
    protected void displayHand(final Activity context, final HashMap<String, Integer> CardReferenceMap, TextView t)
    {
        if(myParticipantId.equals(currentParticipantId))
        {
            Log.d(TAG, "==== Player Play");
            final Hand playerHand = mTurnData.players.get(myParticipantId);
            LinearLayout handLayout1 = (LinearLayout) context.findViewById(R.id.handLayout1);
            if(handLayout1.getChildCount() > 0)
                handLayout1.removeAllViews();
            for (int i = 0; i < playerHand.getCardCount(); i++) {
                String ResourceString = playerHand.getCard(i).getResourceString();
                ImageView imageView = new ImageView(context);
                imageView.setImageResource
                        (CardReferenceMap.get(ResourceString));
                imageView.setClickable(true);
                final int finalI = i;
                imageView.setOnClickListener(new MyOnClickListener(finalI, playerHand, t, context, CardReferenceMap) {
                    @Override
                    public void onClick(View v) {
                        if (!currentParticipantId.equals(myParticipantId)) {//mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN) {
                            Log.d(TAG, "*** Alas: It's not your turn.");
                            showWarning("Alas...", "It's not your turn.");
                        } else {
                            mTurnData.cardReference = finalI;
                            currentPlay(context, CardReferenceMap, playerHand.getCard(finalI));
                        }
                    }
                });
                handLayout1.addView(imageView);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 10, 10);
                final float scale = context.getResources().getDisplayMetrics().density;
                int height_dps = 100;
                int width_dps = 80;
                int height_pixels = (int) (height_dps * scale + 0.5f);
                int width_pixels = (int) (width_dps * scale + 0.5f);imageView.setLayoutParams(lp);imageView.getLayoutParams().height=height_pixels;
                imageView.getLayoutParams().width=width_pixels;
            }
            //cardAccepted = false;
        }
        else
        {
            Log.d(TAG, "==== CPU Play");
            Hand cpuHand = mTurnData.players.get(currentParticipantId);
            int i = 0;
            Gson gson = new Gson();

            do
            {
                Log.d(TAG, "==== CPU while");
                Log.d(TAG, "*** i = " + gson.toJson(i));
                Log.d(TAG, "*** nCards = " + gson.toJson(cpuHand.getCardCount()));
                currentPlay(this, DjamboTurn.CardReferenceMap, cpuHand.getCard(i));
                i++;
                Log.d(TAG, "*** cAcc = "+gson.toJson(cardAccepted));
            }
            while(!cardAccepted  && i<cpuHand.getCardCount());
        }
    }

    protected void currentPlay(Activity context, HashMap<String, Integer> CardReferenceMap,  Card chosenCard)
    {
        Log.d(TAG, "==== Current Play");
        Hand playerHand = mTurnData.players.get(currentParticipantId);

        boolean foundSuit;
        int chosenSuit  = chosenCard.getSuit();

        if(mTurnData.lastCards.isEmpty())//INITIATIVE TURN
        {
            Log.d(TAG, "==== Initiative");
            if(mTurnData.turn==1) mTurnData.nInitiativeId = currentParticipantId;
            mTurnData.turnSuit = chosenSuit;
            mTurnData.players.get(currentParticipantId).removeCard(chosenCard);
            mTurnData.plays.get(currentParticipantId).addCard(chosenCard);
            mTurnData.lastCards.put(currentParticipantId, chosenCard);
            
            whenDone();
        }
        else if (mTurnData.lastCards.size()<mTurnData.players.size()-1)//MIdDLE TURN
        {
            Log.d(TAG, "==== Middle Turn");
            foundSuit = playerHand.findSuit(mTurnData.turnSuit);

            if(foundSuit && chosenSuit!=mTurnData.turnSuit) {
                Log.d(TAG, "==== Nope: Carte refusée");
                cardAccepted = false;
                //displayHand(context, CardReferenceMap, gameInfo);
            }
            else//Turn Play
            {
                cardAccepted = true;
                mTurnData.players.get(currentParticipantId).removeCard(chosenCard);
                mTurnData.plays.get(currentParticipantId).addCard(chosenCard);
                mTurnData.lastCards.put(currentParticipantId, chosenCard);
                
                whenDone();
            }
        }
        else //END TURN
        {
            Log.d(TAG, "==== LAst Turn");
            mTurnData.turnSuit  = mTurnData.lastCards.get(mTurnData.nInitiativeId).getSuit();
            foundSuit = playerHand.findSuit(mTurnData.turnSuit);

            if(foundSuit && chosenSuit!=mTurnData.turnSuit) {
                Log.d(TAG, "==== Nope: Carte refusée");
                cardAccepted = false;
            }
            else//Turn Play
            {
                cardAccepted = true;
                mTurnData.lastCards.put(currentParticipantId, chosenCard);
                mTurnData.players.get(currentParticipantId).removeCard(chosenCard);
                mTurnData.plays.get(currentParticipantId).addCard(chosenCard);

                int currentMaxValue = 0;
                String winnerTurnId="";
                for (HashMap.Entry<String, Card> entry : mTurnData.lastCards.entrySet())
                {
                    if (entry.getValue().getSuit()== mTurnData.turnSuit && entry.getValue().getValue()>currentMaxValue)
                    {
                        currentMaxValue = entry.getValue().getValue();
                        winnerTurnId = entry.getKey();
                    }
                }
                mTurnData.nInitiativeId = winnerTurnId;
                Log.d(TAG, "==== nInitiativeId: " + mTurnData.nInitiativeId);
                if (mTurnData.turn == mTurnData.finalTurn)//end Of Round
                {
                    int roundsCount = mTurnData.roundsWon.get(mTurnData.nInitiativeId);
                    roundsCount++;
                    mTurnData.roundsWon.put(mTurnData.nInitiativeId, roundsCount);

                    if (mTurnData.round == mTurnData.nRounds)//end of Game
                    {
                        
                        mTurnData.lastCards.clear();
                        whenDone();
                        return;
                    }
                    mTurnData.round++;
                    mTurnData.nextRound(participantIds);
                }
                mTurnData.lastCards.clear();
                Log.d(TAG, "==== EndOfTurn");
                
                whenDone();
            }
        }
    }

    protected String getNextParticipantId() {

        Log.d("GNP", "in");

        if(mTurnData.lastCards.isEmpty())
        {
            Log.d("GNP" , "EndTurn");
            return mTurnData.nInitiativeId;
        }

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(currentParticipantId)) {
                desiredIndex = (i + 1)% participantIds.size();
            }
        }
        Log.d("GNP", "dI: "+desiredIndex);

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }
        return null;

    }


    protected void showSpinner() {
        //findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
    }
    protected void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        AlertDialog mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }
}
