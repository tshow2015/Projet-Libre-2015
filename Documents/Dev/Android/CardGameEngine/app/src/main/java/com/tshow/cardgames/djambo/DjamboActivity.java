package com.tshow.cardgames.djambo;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.gson.Gson;
import com.tshow.R;
import com.tshow.cardgameengine.cardelement.Card;
import com.tshow.cardgameengine.abstractbase.GameActivity;
import com.tshow.cardgameengine.cardelement.Hand;
import com.tshow.cardgameengine.MyOnClickListener;
import com.tshow.cardgameengine.abstractbase.GameActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jordantankoua on 01/06/15.
 */
public class DjamboActivity extends GameActivity {
    private static final String TAG = "DjamboActivity";

    private TextView gameInfo;
    private DjamboTurn mTurnData;
    private String winnerGame;



    public void startMatch(TurnBasedMatch match) {
        // Some basic turn data
        mMatch = match;
        displayGameLayout();
        findViewById(R.id.parameters_panel).setVisibility(View.VISIBLE);

        findViewById(R.id.text_nplayers).setVisibility(View.GONE);
        findViewById(R.id.spinner_nplayers).setVisibility(View.GONE);
        findViewById(R.id.text_level).setVisibility(View.GONE);
        findViewById(R.id.radio_level).setVisibility(View.GONE);

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.BELOW, R.id.text_panel);
        findViewById(R.id.text_nrounds).setLayoutParams(p);

    }
    public void onPlayClicked(View view)
    {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        final Spinner spinnerNRounds = (Spinner) findViewById(R.id.spinner_nrounds);
        final Spinner spinnerNCards = (Spinner) findViewById(R.id.spinner_ncards);
        int nRounds = Integer.parseInt(spinnerNRounds.getSelectedItem().toString());
        int nCards = Integer.parseInt(spinnerNCards.getSelectedItem().toString());
        mTurnData = new DjamboTurn(mMatch.getParticipantIds(),nRounds,nCards,0);
        findViewById(R.id.parameters_panel).setVisibility(View.GONE);
        showSpinner();

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mMatch.getMatchId(),
                mTurnData.persist(), myParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
    }
    public void setGameplayUI() {

        isDoingTurn = true;
        setViewVisibility();

        Log.d(TAG, "==== Round: " + mTurnData.round);
        Log.d(TAG, "==== Turn: " + mTurnData.turn);
        foreplay(this, DjamboTurn.CardReferenceMap);
    }

    protected void foreplay(final Activity context, HashMap<String, Integer> CardReferenceMap)
    {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        if (mTurnData == null) Log.d(TAG, "*** Data null");

        JSONObject retVal = new JSONObject();
        Gson gson = new Gson();
        Log.d(TAG, "*** "+gson.toJson(mTurnData.players));
        Hand playerHand = mTurnData.players.get(myParticipantId);

        gameInfo = (TextView) context.findViewById(com.tshow.R.id.gameInfo);
        Log.d(TAG, "*** ZONE");

        LinearLayout tableLayout = (LinearLayout) context.findViewById(com.tshow.R.id.tableLayout);
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
            Log.d(TAG, "*** myP:" + myParticipantId);
            Log.d(TAG, "*** wG:"  + winnerGame);
            Log.d(TAG, "*** M.gS:"  + mMatch.getStatus());
            if(myParticipantId.equals(winnerGame))
                whenFinish();
            else if (mMatch.getStatus()==TurnBasedMatch.MATCH_STATUS_COMPLETE)
                whenFinish();
            return;
        }
        else Log.d(TAG, "*** not FINISH ");
        displayHand(context, CardReferenceMap, null);
    }
    protected void displayInfo()
    {
        String stRoundsWon="";
        for (HashMap.Entry<String, Integer> entry : mTurnData.roundsWon.entrySet())
        {
            stRoundsWon +=entry.getKey()+": "+(entry.getValue())+" round(s)"+" ||| ";
        }
        stRoundsWon += "\n";
        gameInfo.setText(
                        "Round: " + (mTurnData.round) + " ||| "+
                        "Turn: " + (mTurnData.turn) + " ||| "+
                        "Initiative: " + (mTurnData.nInitiativeId) + "\n"+
                        stRoundsWon
                        //"No Cards: " + playerHand.getCardCount() + " ||| "+
                        //"\n"
        );
    }
    protected void displayHand(final Activity context, final HashMap<String, Integer> CardReferenceMap, TextView t)
    {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        final Hand playerHand = mTurnData.players.get(myParticipantId);
        LinearLayout handLayout1 = (LinearLayout) context.findViewById(com.tshow.R.id.handLayout1);
        if(handLayout1.getChildCount() > 0)
            handLayout1.removeAllViews();
        for(int i=0; i<playerHand.getCardCount();i++)
        {
            String ResourceString = playerHand.getCard(i).getResourceString();
            ImageView imageView = new ImageView(context);
            imageView.setImageResource
                    (CardReferenceMap.get(ResourceString));
            imageView.setClickable(true);
            final int finalI = i;
            imageView.setOnClickListener(new MyOnClickListener(finalI, playerHand, t, context, CardReferenceMap) {
                @Override
                public void onClick(View v) {
                    if (mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN) {
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
            int width_pixels = (int) (width_dps * scale + 0.5f);
            imageView.setLayoutParams(lp);
            imageView.getLayoutParams().height=height_pixels;
            imageView.getLayoutParams().width=width_pixels;
        }
    }

    protected void currentPlay(Activity context, HashMap<String, Integer> CardReferenceMap,  Card chosenCard)
    {
        Log.d(TAG, "==== Current Play");
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);
        Hand playerHand = mTurnData.players.get(myParticipantId);

        boolean foundSuit;
        int chosenSuit  = chosenCard.getSuit();

        if(mTurnData.lastCards.isEmpty())//INITIATIVE TURN
        {
            Log.d(TAG, "==== Initiative");
            if(mTurnData.turn==1) mTurnData.nInitiativeId = myParticipantId;
            mTurnData.turnSuit = chosenSuit;
            mTurnData.players.get(myParticipantId).removeCard(chosenCard);
            mTurnData.plays.get(myParticipantId).addCard(chosenCard);
            mTurnData.lastCards.put(myParticipantId, chosenCard);
            whenDone();
        }
        else if (mTurnData.lastCards.size()<mTurnData.players.size()-1)//MIDDLE TURN
        {
            Log.d(TAG, "==== Middle Turn");
            foundSuit = playerHand.findSuit(mTurnData.turnSuit);

            if(foundSuit && chosenSuit!=mTurnData.turnSuit)
            {
                Log.d(TAG, "==== Nope: Carte refusée");
                displayHand(context, CardReferenceMap, gameInfo);
            }
            else//Turn Play
            {
                mTurnData.players.get(myParticipantId).removeCard(chosenCard);
                mTurnData.plays.get(myParticipantId).addCard(chosenCard);
                mTurnData.lastCards.put(myParticipantId, chosenCard);
                whenDone();
            }
        }
        else //END TURN
        {
            Log.d(TAG, "==== LAst Turn");
            mTurnData.turnSuit  = mTurnData.lastCards.get(mTurnData.nInitiativeId).getSuit();
            foundSuit = playerHand.findSuit(mTurnData.turnSuit);

            if(foundSuit && chosenSuit!=mTurnData.turnSuit)
            {
                Log.d(TAG, "==== Nope: Carte refusée");
                displayHand(context, CardReferenceMap, gameInfo);
            }
            else//Turn Play
            {
                mTurnData.lastCards.put(myParticipantId, chosenCard);
                mTurnData.players.get(myParticipantId).removeCard(chosenCard);
                mTurnData.plays.get(myParticipantId).addCard(chosenCard);

                int currentMaxValue = 0;
                String winnerTurnID="";
                for (HashMap.Entry<String, Card> entry : mTurnData.lastCards.entrySet())
                {
                    if (entry.getValue().getSuit()== mTurnData.turnSuit && entry.getValue().getValue()>currentMaxValue)
                    {
                        currentMaxValue = entry.getValue().getValue();
                        winnerTurnID = entry.getKey();
                    }
                }
                mTurnData.nInitiativeId = winnerTurnID;
                Log.d(TAG, "==== nInitiativeId: " + mTurnData.nInitiativeId);
                if (mTurnData.turn == mTurnData.finalTurn)//end Of Round
                {
                    String winnerRound = mTurnData.nInitiativeId;

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
                    mTurnData.nextRound(mMatch.getParticipantIds());
                }
                mTurnData.lastCards.clear();
                whenDone();
            }
        }
    }

    protected String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        if(mTurnData.lastCards.isEmpty())
        {
            Log.d(TAG, "==== GNP - EndTurn");
            return mTurnData.nInitiativeId;
        }
        
        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    public void whenDone() {
        Log.d(TAG, "==== whenDone ");
        mTurnData.turn++;
        showSpinner();

        String nextParticipantId = getNextParticipantId();
        Log.d(TAG, "==== Id: " + nextParticipantId);
        showSpinner();
        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mMatch.getMatchId(),
                mTurnData.persist(), nextParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
        mTurnData = null;
    }

    public void onTurnBasedMatchReceived(TurnBasedMatch match) {
        mMatch = match;
        Toast.makeText(this, "A match was updated.", TOAST_DELAY).show();
        Log.d(TAG, "==== turn: " + "NOTmyturn, unpersist");
        mTurnData = DjamboTurn.unpersist(mMatch.getData());
        setGameplayUI();
    }

    public void displayGameLayout()
    {
        setContentView(R.layout.activity_djambo);
    }

    public void updateMatch(TurnBasedMatch match) {
        if(mTurnData==null) Log.d(TAG, "==== mT Null 1");

        mMatch = match;
        Log.d(TAG, "==== update");

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                Log.d(TAG, "==== MATCH_STATUS_COMPLETE");
                //if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE)
                //{
                    if(mMatch.getStatus()==TurnBasedMatch.MATCH_STATUS_COMPLETE)
                    {
                        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
                        String myParticipantId = mMatch.getParticipantId(playerId);
                        if(mTurnData==null) Log.d(TAG, "==== mT Null 2");
                        Log.d(TAG, "==== winnerGame: "+ winnerGame);
                        if(myParticipantId.equals(winnerGame))
                            showWarning("YES", "YOU'VE WON THE GAME");
                        else showWarning("SORRY", "YOU LOST.");
                    }
                    else {
                        showWarning(
                                "Complete!",
                                "This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    }
                    break;
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                Log.d(TAG, "==== turn: " + "unpin");
                mTurnData = DjamboTurn.unpersist(mMatch.getData());
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                mTurnData = DjamboTurn.unpersist(mMatch.getData());
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good inititative!","Still waiting for invitations.\n\nBe patient!");
        }

        mTurnData = null;

        setViewVisibility();
    }
}
