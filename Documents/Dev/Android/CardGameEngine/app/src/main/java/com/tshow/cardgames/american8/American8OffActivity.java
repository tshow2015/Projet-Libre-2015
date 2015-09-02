package com.tshow.cardgames.american8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tshow.R;
import com.tshow.cardgameengine.cardelement.Card;
import com.tshow.cardgameengine.abstractbase.GameTurn;
import com.tshow.cardgameengine.cardelement.Hand;
import com.tshow.cardgameengine.MyOnClickListener;
import com.tshow.cardgames.american8.American8Turn;
import com.tshow.cardgameengine.abstractbase.GameOffActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by jordantankoua on 01/06/15.
 */

public class American8OffActivity extends GameOffActivity {
    private static final String TAG = "American8OffActivity";
    private American8Turn mTurnData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_american8);
        findViewById(R.id.parameters_panel).setVisibility(View.VISIBLE);
    }

    public void onPlayClicked(View view) {
        final Spinner spinnerNPlayers = (Spinner) findViewById(R.id.spinner_nplayers);

        final Spinner spinnerNCards = (Spinner) findViewById(R.id.spinner_ncards);
        final RadioGroup radioGLevel = (RadioGroup) findViewById(R.id.radio_level);
        ArrayList<RadioButton> radioButtons=new ArrayList<>();
        int nCPUParticipants = Integer.parseInt(spinnerNPlayers.getSelectedItem().toString()) - 1;
        Log.d(TAG, "RGL: " + radioGLevel.getCheckedRadioButtonId());

        int nCards = Integer.parseInt(spinnerNCards.getSelectedItem().toString());
        int level = (radioGLevel.getCheckedRadioButtonId()<1) ? 1 : radioGLevel.getCheckedRadioButtonId();
        level-=2;
        radioGLevel.clearCheck();
        myParticipantId = "player";
        currentParticipantId = myParticipantId;
        participantIds = new ArrayList<>();
        participantIds.add(myParticipantId);
        for (int i = 0; i < nCPUParticipants; i++) {
            participantIds.add("cpu" + i);
        }
        findViewById(R.id.parameters_panel).setVisibility(View.GONE);

        mTurnData = new American8Turn(participantIds, nCards, level);
        foreplay(this, American8Turn.CardReferenceMap);
    }

    public void onFinishClicked(View view) {
        Intent iT = new Intent(this, American8Activity.class);
        startActivity(iT);
        finish(); // Call once you redirect to another activity
    }

    protected void whenDone() {
        Log.d(TAG, "==== wHenDone ");
        mTurnData.turn++;
        showSpinner();
        currentParticipantId = getNextParticipantId();
        showSpinner();
        foreplay(this, American8Turn.CardReferenceMap);
    }

    protected void whenFinish() {
        showSpinner();
        Log.d(TAG, "whenFN");
        winnerGame = currentParticipantId;
        setContentView(R.layout.activity_american8);
        if (winnerGame.equals(myParticipantId))
            showWarning("YES", "YOU'VE WON THE GAME");
        else showWarning("SORRY", "YOU LOST.\nPlayer " + winnerGame + " WON.");
    }

    protected void foreplay(final Activity context, HashMap<String, Integer> CardReferenceMap) {
        setContentView(R.layout.activity_american8);
        Log.d(TAG, "######### TURN: " + mTurnData.turn);
        Log.d(TAG, "######### Id: " + currentParticipantId);
        if (mTurnData == null) Log.d(TAG, "*** Data null");
        Hand playerHand = mTurnData.players.get(currentParticipantId);
        gameInfo = (TextView) context.findViewById(com.tshow.R.id.gameInfo);

        ImageView currentCardView = (ImageView) context.findViewById(R.id.currentCard);
        if (mTurnData.currentCard == null) Log.d(TAG, "*** fp: du null");
        if (currentCardView == null) Log.d(TAG, "*** CCV: du null");
        if (American8Turn.CardReferenceMap == null) Log.d(TAG, "*** CRM: du null");

        String tempString = mTurnData.currentCard.getResourceString();
        int cardIdinMap = American8Turn.CardReferenceMap.get(tempString);
        currentCardView.setImageResource(cardIdinMap);

        ImageView pileDeck = (ImageView) context.findViewById(R.id.pileDeck);
        pileDeck.setImageResource(com.tshow.R.drawable.back);
        pileDeck.setClickable(true);
        pileDeck.setOnClickListener(new MyOnClickListener(-1, playerHand, gameInfo, context, American8Turn.CardReferenceMap) {
            @Override
            public void onClick(View v) {

                if (!currentParticipantId.equals(myParticipantId)) {
                    Log.d(TAG, "*** Alas: It's not your turn.");
                    showWarning("Alas...", "It's not your turn.");
                } else {
                    mTurnData.cardReference = -1;
                    currentPlay(context, American8Turn.CardReferenceMap, null);
                }
            }
        });
        displayInfo();
        displayHand(context, American8Turn.CardReferenceMap, null);
    }

    protected void displayInfo() {
        String nCPUCards = "";
        for (HashMap.Entry<String, Hand> entry : mTurnData.players.entrySet()) {
            if (!myParticipantId.equals(entry.getKey())) {
                nCPUCards += entry.getKey() + ": " + entry.getValue().getCardCount() + "    ";
            }
        }
        gameInfo.setText(
                "Turn: " + (mTurnData.turn) + "    "
                        //+ "Turn: Player: " + (currentPlayer) + "\n"
                        + "Penalty: " + mTurnData.account + "    "
                        + "LcS: " + mTurnData.commandSuit + "\n"
                        + "No Cards::   Me: " + mTurnData.players.get("player").getCardCount() + "    "
                        + nCPUCards//+ "No CardsNxCPU: " + mTurnData.players.get(getNextParticipantId()).getCardCount() + " ||| "
                        //+ "Next Card: " + mTurnData.deck.peekCard().getValueAsString() + " of " + mTurnData.deck.peekCard().getSuitAsString()
                        + "\n");
    }

    protected void displayHand(final Activity context, final HashMap<String, Integer> CardReferenceMap, TextView t) {
        mTurnData.level = 1;
        Log.d(TAG, "==== level: "+mTurnData.level);
        if (myParticipantId.equals(currentParticipantId)) {
            Log.d(TAG, "==== Player Play");
            final Hand playerHand = mTurnData.players.get(myParticipantId);
            LinearLayout handLayout = (LinearLayout) context.findViewById(com.tshow.R.id.handLayout);
            if (handLayout.getChildCount() > 0)
                handLayout.removeAllViews();
            for (int i = 0; i < playerHand.getCardCount(); i++) {
                String ResourceString = playerHand.getCard(i).getResourceString();
                ImageView imageView = new ImageView(context);
                imageView.setImageResource
                        (American8Turn.CardReferenceMap.get(ResourceString));
                imageView.setClickable(true);
                final int finalI = i;
                imageView.setOnClickListener(new MyOnClickListener(finalI, playerHand, t, context, American8Turn.CardReferenceMap) {
                    @Override
                    public void onClick(View v) {
                        if (!currentParticipantId.equals(myParticipantId)) {
                            Log.d(TAG, "*** Alas: It's not your turn.");
                            showWarning("Alas...", "It's not your turn.");
                        } else {
                            Gson gson = new Gson();
                            Log.d(TAG, "*** chosenCard: " + gson.toJson(playerHand.getCard(finalI)));
                            mTurnData.cardReference = finalI;
                            currentPlay(context, American8Turn.CardReferenceMap, playerHand.getCard(finalI));
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
                imageView.getLayoutParams().height = height_pixels;
                imageView.getLayoutParams().width = width_pixels;
            }
        }
        else if(mTurnData.level==1)
        {
            Log.d(TAG, "==== CPU Play");
            Log.d(TAG, "==== level: "+mTurnData.level);
            Hand cpuHand = mTurnData.players.get(currentParticipantId);
            int i = 0;
            mTurnData.cardAccepted = false;
            while (!mTurnData.cardAccepted && i < cpuHand.getCardCount()) {
                Log.d(TAG, "==== CPU while-" + i);
                Gson gson = new Gson();
                Log.d(TAG, "*** curr: " + gson.toJson(mTurnData.currentCard));
                Log.d(TAG, "*** choz: " + gson.toJson(cpuHand.getCard(i)));
                mTurnData.cardReference = i;
                currentPlay(this, American8Turn.CardReferenceMap, cpuHand.getCard(i));
                i++;

            }
            if (!myParticipantId.equals(currentParticipantId) && i == cpuHand.getCardCount()) {
                mTurnData.cardAccepted = true;
                Log.d(TAG, "==== CPU pick-" + i);
                mTurnData.cardReference = -1;
                currentPlay(this, American8Turn.CardReferenceMap, null);
            }
        }
        else if (mTurnData.level==2)
        {
            Log.d(TAG, "==== CPU Play");
            Log.d(TAG, "==== level: "+mTurnData.level);

            Card cardToPlay = chooseCardAI();
            mTurnData.cardReference = mTurnData.players.get(currentParticipantId).findReference(cardToPlay);

            Gson gson = new Gson();
            Log.d(TAG, "*** chosenCard: " + gson.toJson(cardToPlay));
            Log.d(TAG, "*** cardRef: " + gson.toJson(mTurnData.cardReference));

            currentPlay(this, American8Turn.CardReferenceMap, cardToPlay);
        }
        else Log.d(TAG, "==== ERROR Player To Play");

    }

    private static int cPCounter = 0;

    protected void currentPlay(Activity context, HashMap<String, Integer> CardReferenceMap, Card chosenCard) {
        Log.d(TAG, "==== currentPlay");
        Gson gson = new Gson();
        Log.d(TAG, "*** curr: " + gson.toJson(mTurnData.currentCard));
        Log.d(TAG, "*** pl: " + gson.toJson(mTurnData.players));
        cPCounter++;

        int chosenSuit = -2;
        int chosenValue = -2;
        if (chosenCard != null) {
            chosenSuit = chosenCard.getSuit();
            chosenValue = chosenCard.getValue();
        }

        if ((mTurnData.currentCard.getValue() == 1 || mTurnData.currentCard.getValue() == 14) && !mTurnData.cardConsidered) {
            if (mTurnData.cardReference == -1 || American8Turn.afterAceJoker.contains(chosenValue)) {
                mTurnData.cardAccepted = true;
                mTurnData.adderCard = true;
                nextPlay(context, chosenCard, mTurnData.adderCard);
            } else {
                mTurnData.cardAccepted = false;
            }

        }
        else if (mTurnData.currentCard.getValue() == 14 && mTurnData.cardConsidered)
        {
            if(chosenValue == 14 || chosenSuit % 2 == mTurnData.currentCard.getSuit()) {
                mTurnData.currentCard = chosenCard;
                mTurnData.players.get(currentParticipantId).removeCard(mTurnData.currentCard);
                mTurnData.deck.addUsedCard(mTurnData.currentCard);
                mTurnData.cardAccepted = true;
            }
            else{
                mTurnData.cardAccepted = false;
            }

        }

        else {
            if ((mTurnData.cardReference == -1 && !mTurnData.deck.isEmpty()) ||
                    chosenSuit == mTurnData.currentCard.getSuit() ||
                    chosenValue == mTurnData.currentCard.getValue() ||
                    American8Turn.passePartouts.contains(chosenValue)) {
                mTurnData.cardAccepted = true;
                Log.d(TAG, "in cP. Will Pick");
                mTurnData.adderCard = false;
                nextPlay(context, chosenCard, mTurnData.adderCard);
            } else {
                mTurnData.cardAccepted = false;
            }
        }

        for (HashMap.Entry<String, Hand> entry : mTurnData.players.entrySet()) {
            String participantId = entry.getKey();
            Hand hand = entry.getValue();
            // ...
            if (hand.getCardCount() == 0) {
                whenFinish();
                return;
            }
        }

        if (!mTurnData.cardAccepted) {
            Log.d(TAG, "==== Nope: Carte refusée");
        }
        else if (winnerGame == null)
        {
            if (mTurnData.commandSuit == -2) {
                if (currentParticipantId.equals(myParticipantId)) {
                    TextView commandGreeting = (TextView) context.findViewById(R.id.commandGreeting);

                    if (mTurnData.adderCard) {
                        commandGreeting.setText("You blocked the attack.\nWhich suit do you want to command?  ");
                    } else {
                        commandGreeting.setText("Which suit do you want to command?  ");
                    }
                    findViewById(R.id.command_layout).setVisibility(View.VISIBLE);
                } else {
                    mTurnData.commandSuit = mTurnData.players.get(currentParticipantId).getCard(0).getSuit();
                    whenDone();
                }
            }
            else {
                whenDone();
            }
        }
        Log.d(TAG, "=== endOfcP. acc: " + gson.toJson(mTurnData.cardAccepted));
    }


    protected void nextPlay(Activity context, Card chosenCard, boolean adderCard) {

        if (mTurnData.cardReference == -1 && adderCard) {
            for (int i = 0; i < mTurnData.account; i++) {
                mTurnData.players.get(currentParticipantId).addCard(mTurnData.deck.dealCard());
            }
            mTurnData.account = 0;
            mTurnData.cardConsidered = true;
        } else if (mTurnData.cardReference == -1 && true) {
            mTurnData.players.get(currentParticipantId).addCard(mTurnData.deck.dealCard());
        } else if (chosenCard.getValue() == 7) {
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(currentParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
            mTurnData.skipNext = true;
        } else if (chosenCard.getValue() == 10) {
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(currentParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
            mTurnData.incOrder = !mTurnData.incOrder;
        } else if (chosenCard.getValue() == Card.ACE) {
            mTurnData.cardConsidered = false;
            mTurnData.account += 2;
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(currentParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
        } else if (mTurnData.players.get(currentParticipantId).getCard(mTurnData.cardReference).getValue() == Card.JOKER) {
            mTurnData.cardConsidered = false;
            mTurnData.account += 4;
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(currentParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
        }
        else {
            mTurnData.currentCard = chosenCard;
            mTurnData.players.get(currentParticipantId).removeCard(mTurnData.currentCard);
            mTurnData.deck.addUsedCard(mTurnData.currentCard);
        }
    }


    public void onSpadesClicked(View view) {
        mTurnData.commandSuit = 0;
        whenDone();
    }

    public void onHeartsClicked(View view) {
        mTurnData.commandSuit = 1;
        whenDone();
    }

    public void onClubsClicked(View view) {
        mTurnData.commandSuit = 2;
        whenDone();
    }

    public void onDiamondsClicked(View view) {
        mTurnData.commandSuit = 3;
        whenDone();
    }

    protected String getNextParticipantId() {

        int desiredIndex = -1;

        if (mTurnData.incOrder && !mTurnData.skipNext) {
            for (int i = 0; i < participantIds.size(); i++) {
                if (participantIds.get(i).equals(currentParticipantId)) {
                    //desiredIndex = i + 1;
                    desiredIndex = (i + 1) % participantIds.size();
                }
            }
        }
        if (mTurnData.incOrder && mTurnData.skipNext) {
            for (int i = 0; i < participantIds.size(); i++) {
                if (participantIds.get(i).equals(currentParticipantId)) {
                    desiredIndex = (i + 2) % participantIds.size();
                }
            }
            mTurnData.skipNext = false;
        }
        if (!mTurnData.incOrder && !mTurnData.skipNext) {
            for (int i = 0; i < participantIds.size(); i++) {
                if (participantIds.get(i).equals(currentParticipantId)) {
                    int mod = (i - 1) % participantIds.size();
                    if (mod < 0) mod += participantIds.size();
                    desiredIndex = mod;
                }
            }
        }
        if (!mTurnData.incOrder && mTurnData.skipNext) {
            for (int i = 0; i < participantIds.size(); i++) {
                if (participantIds.get(i).equals(currentParticipantId)) {
                    int mod = (i - 2) % participantIds.size();
                    if (mod < 0) mod += participantIds.size();
                    desiredIndex = mod;
                }
            }
            mTurnData.skipNext = false;
        }

        //Log.d(TAG, "==== getNP: " + desiredIndex);

        if (desiredIndex > -1 && desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }
        Log.d(TAG, "==== getNP: " + "Danger");
        return null;
    }

    protected void showSpinner() {
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
    private void computeProbabilities(double[] suitsProbs, double[] valuesProbs)
    {
        ArrayList<Card> usedCards = new ArrayList<Card>();
        usedCards.addAll(mTurnData.deck.getUsedDeck());
        usedCards.addAll(mTurnData.players.get(myParticipantId).getHand());
        for(int i=0;i<4;i++)
            suitsProbs[i]=13;
        for(int i=0;i<13;i++)
            valuesProbs[i]=4;
        valuesProbs[13]=2;

        for(int i=0;i<usedCards.size();i++)
        {
            valuesProbs[usedCards.get(i).getValue()-1]-=1;
            if(usedCards.get(i).getValue()<14)
                suitsProbs[usedCards.get(i).getSuit()]-=1;
        }
        for(int i=0;i<usedCards.size();i++)
        {
            valuesProbs[usedCards.get(i).getValue()-1]/=(54-usedCards.size());
            if(usedCards.get(i).getValue()<14)
                suitsProbs[usedCards.get(i).getSuit()]/=(54-usedCards.size());
        }
    }
    private Card nonSpecialAIPlay()
    {
        if(mTurnData.adderCard) return null;

        Hand playerAI = mTurnData.players.get(currentParticipantId);
        int numCardsAI = playerAI.getCardCount();
        Hand specialCardsAI = new Hand();
        Hand nonSpecialCardsAI = new Hand();
        double[] suitsProbs = new double[4];
        double[] valuesProbs = new double[14];
        int suitToPlay= (mTurnData.commandSuit>-1) ? mTurnData.commandSuit : mTurnData.currentCard.getSuit();

        computeProbabilities(suitsProbs, valuesProbs);

        for(int i =0;i<numCardsAI;i++)
        {
            int suit = playerAI.getCard(i).getSuit();
            int value = playerAI.getCard(i).getValue();

            playerAI.getCard(i).setProbability(suitsProbs[suit]+valuesProbs[value-1]-suitsProbs[suit]*valuesProbs[value-1]);

            if(American8Turn.specialCards.contains(playerAI.getCard(i))) {
                specialCardsAI.addCard(playerAI.getCard(i));
            }
            if(!American8Turn.specialCards.contains(playerAI.getCard(i))) {
                nonSpecialCardsAI.addCard(playerAI.getCard(i));
            }
        }
        Gson gson = new Gson();
        nonSpecialCardsAI.sortByProb();
        Log.d(TAG, "*** playerAI: " + gson.toJson(playerAI));
        for(int i =0;i<nonSpecialCardsAI.getCardCount();i++)
        {
            if (mTurnData.currentCard.getSuit() == nonSpecialCardsAI.getCard(i).getSuit()
                    || mTurnData.currentCard.getValue() == nonSpecialCardsAI.getCard(i).getValue()) {
                return nonSpecialCardsAI.getCard(i);
            }
        }
        if(specialCardsAI.hasCardValue(2)) return specialCardsAI.getCardbyValue(2);
        if(specialCardsAI.hasCardValue(10,suitToPlay))
        if(specialCardsAI.hasCardValue(7,suitToPlay))
        if(specialCardsAI.hasCardValue(Card.ACE,suitToPlay))
        if(specialCardsAI.hasCardValue(Card.JOKER)) return specialCardsAI.getCardbyValue(Card.JOKER);
        if(specialCardsAI.hasCardValue(8)) {
            mTurnData.commandSuit=playerAI.mostCommonSuit();
            return specialCardsAI.getCardbyValue(8);
        }
        return null;
    }

    private Card chooseCardAI() {
        int nextPartNumCards = mTurnData.players.get(getNextParticipantId()).getCardCount();
        Hand playerAI = mTurnData.players.get(currentParticipantId);
        int numCardsAI = playerAI.getCardCount();
        int suitToPlay= (mTurnData.commandSuit>-1) ? mTurnData.commandSuit : mTurnData.currentCard.getSuit();

        if (!mTurnData.adderCard)
        {
            if (nextPartNumCards <= 3)
            {
                if (numCardsAI <= 3) {
                    if (mTurnData.players.size() == 2)
                    {
                        if (playerAI.hasCardValue(7,suitToPlay))
                            return playerAI.getMatchingCard(7, suitToPlay);
                        if (playerAI.hasCardValue(10,suitToPlay))
                            return playerAI.getMatchingCard(10, suitToPlay);
                        if (playerAI.hasCardValue(Card.ACE,suitToPlay))
                            return playerAI.getMatchingCard(Card.ACE, suitToPlay);
                        if (playerAI.hasCardValue(Card.JOKER))
                            return playerAI.getCardbyValue(Card.JOKER);
                        return nonSpecialAIPlay();

                    }
                    else //nPlayers>2
                    {
                        if (playerAI.hasCardValue(Card.ACE,suitToPlay))
                            return playerAI.getMatchingCard(Card.ACE, suitToPlay);
                        if (playerAI.hasCardValue(Card.JOKER,suitToPlay))
                            return playerAI.getCardbyValue(Card.JOKER);
                        if (playerAI.hasCardValue(7,suitToPlay))
                            return playerAI.getMatchingCard(7, suitToPlay);
                        if (playerAI.hasCardValue(10))
                            return playerAI.getMatchingCard(10, suitToPlay);
                        return nonSpecialAIPlay();
                    }
                }
                else //numCardsAI>3
                {
                    if (mTurnData.players.size() == 2)
                    {
                        if (playerAI.hasCardValue(7,suitToPlay))
                            return playerAI.getMatchingCard(7, suitToPlay);
                        if (playerAI.hasCardValue(10,suitToPlay))
                            return playerAI.getMatchingCard(10, suitToPlay);
                        if (playerAI.hasCardValue(Card.ACE,suitToPlay))
                            return playerAI.getMatchingCard(Card.ACE, suitToPlay);
                        if (playerAI.hasCardValue(Card.JOKER))
                            return playerAI.getCardbyValue(Card.JOKER);
                        return nonSpecialAIPlay();
                    }
                    else //nPlayers>2
                    {
                        return nonSpecialAIPlay();
                    }
                }
            }
            else //nextPartNumCards>3
            {
                nonSpecialAIPlay();
            }
        }
        else //ADDER CARD
        {
            if (nextPartNumCards <= 3) {
                if (numCardsAI <= 3)
                {
                    if (playerAI.hasCardValue(Card.ACE,suitToPlay))
                        return playerAI.getMatchingCard(Card.ACE, suitToPlay);
                    if (playerAI.hasCardValue(Card.JOKER))
                        return playerAI.getCardbyValue(Card.JOKER);
                    if (playerAI.hasCardValue(8))
                        return playerAI.getCardbyValue(8);
                    return nonSpecialAIPlay();

                }
                else //numCardsAI>3
                {
                    if (mTurnData.players.size() == 2)
                    {
                        if (playerAI.hasCardValue(Card.ACE,suitToPlay))
                            return playerAI.getMatchingCard(Card.ACE, suitToPlay);
                        if (playerAI.hasCardValue(Card.JOKER))
                            return playerAI.getCardbyValue(Card.JOKER);
                        if (playerAI.hasCardValue(8))
                            return playerAI.getCardbyValue(8);
                        return nonSpecialAIPlay();
                    }
                    else //nPlayers>2
                    {
                        return nonSpecialAIPlay();
                    }
                }
            }
            else //nextPartNumCards>3
            {
                return nonSpecialAIPlay();
            }
        }
        return null;
    }
}
