package com.tshow.cardgames.american8;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.tshow.R;
import com.tshow.cardgameengine.cardelement.Card;
//import com.tshow.cardgameengine.GameActivity;
import com.tshow.cardgameengine.cardelement.Hand;
import com.tshow.cardgameengine.MyOnClickListener;
import com.tshow.cardgameengine.abstractbase.GameActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jordantankoua on 01/06/15.
 */

public class American8Activity extends GameActivity {
    private static final String TAG = "American8Activity";

    public String winnerGame ="";
    private TextView gameInfo;


    private American8Turn mTurnData;

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

    public void setGameplayUI() {

        isDoingTurn = true;
        setViewVisibility();
        if(mMatch.getStatus()==TurnBasedMatch.MATCH_STATUS_COMPLETE)
        {
            Log.d(TAG, "==== Finish-" + mTurnData.turn);
            whenFinish();
        }

        Log.d(TAG, "==== Turn: " + mTurnData.turn);
        foreplay(this, American8Turn.CardReferenceMap);
    }
    public void initializeGameTurn(TurnBasedMatch match) {
        mTurnData = new American8Turn(match.getParticipantIds());
    }

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
        findViewById(R.id.text_ncards).setLayoutParams(p);
    }
    public void onPlayClicked(View view)
    {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        final Spinner spinnerNCards = (Spinner) findViewById(R.id.spinner_ncards);
        int nCards = Integer.parseInt(spinnerNCards.getSelectedItem().toString());
        mTurnData = new American8Turn(mMatch.getParticipantIds(),nCards,0);
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
    protected void foreplay(final Activity context, HashMap<String, Integer> CardReferenceMap)
    {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);
        if (mTurnData == null) Log.d(TAG, "*** Data null");
        Hand playerHand = mTurnData.players.get(myParticipantId);

        gameInfo = (TextView) context.findViewById(com.tshow.R.id.gameInfo);

        Log.d(TAG, "*** ZONE");
        ImageView currentCardView = (ImageView) context.findViewById(R.id.currentCard);
        if (mTurnData.currentCard == null) Log.d(TAG, "*** fp: du null");
        if (currentCardView == null) Log.d(TAG, "*** CCV: du null");
        if (CardReferenceMap == null) Log.d(TAG, "*** CRM: du null");
        currentCardView.setImageResource(
                CardReferenceMap.get(
                        mTurnData.currentCard.getResourceString()));

        ImageView pileDeck = (ImageView) context.findViewById(R.id.pileDeck);
        pileDeck.setImageResource(com.tshow.R.drawable.back);
        pileDeck.setClickable(true);
        pileDeck.setOnClickListener(new MyOnClickListener(-1, playerHand, gameInfo, context, CardReferenceMap) {
            @Override
            public void onClick(View v) {

                if (mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN) {
                    Log.d(TAG, "*** Alas: It's not your turn.");
                    showWarning("Alas...", "It's not your turn.");
                } else {
                    mTurnData.cardReference = -1;
                    currentPlay(context, American8Turn.CardReferenceMap, null);
                }
            }
        });

        displayInfo();
        displayHand(context, CardReferenceMap, null);
    }
    protected void displayInfo()
    {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);
        if (mTurnData == null) Log.d(TAG, "*** Data null");
        Hand playerHand = mTurnData.players.get(myParticipantId);
        gameInfo.setText(
                "Turn: " + (mTurnData.turn) + " ||| "
                        //+ "Turn: Player: " + (currentPlayer) + "\n"
                        + "Account: " + mTurnData.account + " ||| "
                        + "LcS: " + mTurnData.commandSuit + "\n"
                        + "No Cards: " + playerHand.getCardCount() + " ||| "
                        //+ "Next Card: " + mTurnData.deck.peekCard().getValueAsString() + " of " + mTurnData.deck.peekCard().getSuitAsString()
                        + "\n");
    }
    protected void displayHand(final Activity context, final HashMap<String, Integer> CardReferenceMap, TextView t)
    {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);
        mTurnData.cardAccepted = true;

        final Hand playerHand = mTurnData.players.get(myParticipantId);
        LinearLayout handLayout = (LinearLayout) context.findViewById(com.tshow.R.id.handLayout);
        if(handLayout.getChildCount() > 0)
            handLayout.removeAllViews();
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
                    //t.setText("CHO!" + Integer.toString(i++));


                    if (mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN)
                    {
                        Log.d(TAG, "*** Alas: It's not your turn.");
                        showWarning("Alas...", "It's not your turn.");
                    }
                    else
                    {
                        //NPE when doubleclick too much
                        mTurnData.cardReference = finalI;
                        currentPlay(context, CardReferenceMap, playerHand.getCard(finalI));
                    }


                }
            });
            handLayout.addView(imageView);
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
    }

    protected void currentPlay(Activity context, HashMap<String, Integer> CardReferenceMap,  Card chosenCard)
    {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        int chosenSuit =-2;
        int chosenValue = -2;
        if(chosenCard != null)
        {
            chosenSuit  = chosenCard.getSuit();
            chosenValue = chosenCard.getValue();
        }

        if((mTurnData.currentCard.getValue()==1 || mTurnData.currentCard.getValue()==14)  && !mTurnData.cardConsidered)
        {
            if(mTurnData.cardReference == -1 || American8Turn.afterAceJoker.contains(chosenValue))
            {
                nextPlay(context, chosenCard, true) ;
            }
            else
            {
                mTurnData.cardAccepted = false;
            }

        }
        else if(mTurnData.currentCard.getValue()==14  && mTurnData.cardConsidered && mTurnData.currentCard.getSuit() == chosenSuit%2 )
        {
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(myParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);

        }

        else
        {
            if ((mTurnData.cardReference == -1 && !mTurnData.deck.isEmpty()) ||
                    chosenSuit == mTurnData.currentCard.getSuit() ||
                    chosenValue == mTurnData.currentCard.getValue() ||
                    American8Turn.passePartouts.contains(chosenValue))
            {
                nextPlay(context, chosenCard, false) ;
            }
            else
            {
                mTurnData.cardAccepted = false;
            }
        }

        for (HashMap.Entry<String, Hand> entry : mTurnData.players.entrySet())
        {
            if(entry.getValue().getCardCount()==0)
            {
                gameInfo.setText("Player: "+entry.getKey()+" WON THE GAME !!!!\n\n");
                if (mTurnData.players.get(myParticipantId).getCardCount()
                        ==0) {
                    mTurnData.winnerGame = myParticipantId;
                    winnerGame = myParticipantId;
                }
                whenFinish();
            }
        }

        if (!mTurnData.cardAccepted) {
            Log.d(TAG, "==== Nope: Carte refusée");
            displayHand(context, CardReferenceMap, gameInfo);
        }
        else if (mMatch.getStatus()!=TurnBasedMatch.MATCH_STATUS_COMPLETE) {
            if(mTurnData.commandSuit==-2)
            {
                TextView commandGreeting = (TextView) context.findViewById(R.id.commandGreeting);
                if (mTurnData.adderCard)
                {
                    commandGreeting.setText("You blocked the attack.\nWhich suit do you want to command?  ");
                }
                else
                {
                    commandGreeting.setText("Which suit do you want to command?  ");
                }
                setViewVisibility();
            }
            else
            {
                whenDone();
            }
        }
        Log.d(TAG, "====    cp: End");
    }

    private void nextPlay(Activity context, Card chosenCard, boolean adderCard)
    {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        final String myParticipantId = mMatch.getParticipantId(playerId);

        if(mTurnData.cardReference==-1 && adderCard)
        {
            for (int i = 0; i<mTurnData.account; i++)
            {
                mTurnData.players.get(myParticipantId).addCard(mTurnData.deck.dealCard());
            }
            mTurnData.account = 0;
            mTurnData.cardConsidered = true;
        }
        else if(mTurnData.cardReference==-1 && true)
        {
            mTurnData.players.get(myParticipantId).addCard(mTurnData.deck.dealCard());
        }
        else if (chosenCard.getValue() == 7)
        {
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(myParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
            mTurnData.skipNext=true;
        }
        else if (chosenCard.getValue() == 10)
        {
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(myParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
            mTurnData.incOrder = ! mTurnData.incOrder;
        }
        else if (chosenCard.getValue() == Card.ACE)
        {
            mTurnData.account+=2;
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(myParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
        }
        else if (mTurnData.players.get(myParticipantId).getCard(mTurnData.cardReference).getValue() == Card.JOKER)
        {
            mTurnData.account+=4;
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(myParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
        }
        else
        {
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(myParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
        }
    }


    public void onSpadesClicked(View view){mTurnData.commandSuit=0; whenDone();}
    public void onHeartsClicked(View view){mTurnData.commandSuit=1; whenDone();}
    public void onClubsClicked(View view){mTurnData.commandSuit=2; whenDone();}
    public void onDiamondsClicked(View view){mTurnData.commandSuit=3; whenDone();}

    protected String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        if (mTurnData.incOrder && !mTurnData.skipNext)
        {
            for (int i = 0; i < participantIds.size(); i++) {
                if (participantIds.get(i).equals(myParticipantId)) {
                    //desiredIndex = i + 1;
                    desiredIndex = (i + 1) % participantIds.size();
                }
            }
        }
        if (mTurnData.incOrder && mTurnData.skipNext)
        {
            for (int i = 0; i < participantIds.size(); i++) {
                if (participantIds.get(i).equals(myParticipantId)) {
                    desiredIndex = (i + 2 )% participantIds.size();
                }
            }
            mTurnData.skipNext=false;
        }
        if (!mTurnData.incOrder && !mTurnData.skipNext)
        {
            for (int i = 0; i < participantIds.size(); i++) {
                if (participantIds.get(i).equals(myParticipantId)) {
                    int mod = (i - 1) % participantIds.size();
                    if (mod<0) mod += participantIds.size();
                    desiredIndex = mod ;
                }
            }
        }
        if (!mTurnData.incOrder && mTurnData.skipNext)
        {
            for (int i = 0; i < participantIds.size(); i++) {
                if (participantIds.get(i).equals(myParticipantId)) {
                    int mod = (i - 2) % participantIds.size();
                    if (mod<0) mod += participantIds.size();
                    desiredIndex = mod ;
                }
            }
            mTurnData.skipNext=false;
        }

        Log.d(TAG, "==== getNP: " + desiredIndex);

        if (desiredIndex > -1 && desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }
        Log.d(TAG, "==== getNP: " + "Danger");

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    public void onTurnBasedMatchReceived(TurnBasedMatch match) {
        mMatch = match;
        Toast.makeText(this, "A match was updated.", TOAST_DELAY).show();
        Log.d(TAG, "==== turn: " + "NOTmyturn, unpersist");
        mTurnData = American8Turn.unpersist(mMatch.getData());
        setGameplayUI();
    }

    public void displayGameLayout()
    {
        setContentView(R.layout.activity_american8);
        if (mTurnData!=null && mTurnData.commandSuit==-2)
        {
            findViewById(R.id.command_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.gameplay_layout).setVisibility(View.GONE);
        }
    }

    public void updateMatch(TurnBasedMatch match) {
        mMatch = match;
        Log.d(TAG, "==== update");

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

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
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    //if(mTurnData==null) showWarning("H","mT NULL"); else
                    if(myParticipantId.equals(winnerGame))
                    {
                        showWarning("YES","YOU'VE WON THE GAME");
                    }
                    else {
                        showWarning("SORRY","YOU LOST.");
                        //showWarning("Complete!","This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    }
                    break;
                }
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                Log.d(TAG, "==== turn: " + "unpin");
                mTurnData = American8Turn.unpersist(mMatch.getData());
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                Log.d(TAG, "==== turn: " + "unpin2");
                mTurnData = American8Turn.unpersist(mMatch.getData());
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good inititative!","Still waiting for invitations.\n\nBe patient!");
        }

        mTurnData = null;

        setViewVisibility();
    }
}
