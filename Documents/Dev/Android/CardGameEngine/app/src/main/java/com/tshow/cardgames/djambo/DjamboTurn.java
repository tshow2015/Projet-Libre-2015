package com.tshow.cardgames.djambo;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.tshow.cardgameengine.cardelement.Card;
import com.tshow.cardgameengine.cardelement.Deck;
import com.tshow.cardgameengine.abstractbase.GameTurn;
import com.tshow.cardgameengine.cardelement.Hand;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.tshow.R;

public class DjamboTurn extends GameTurn {

    private static final String TAG = "EBTurn";

    static HashMap<String, Integer> CardReferenceMap;

    public HashMap<String, Hand> players;
    public HashMap<String, Hand> plays;
    public HashMap<String, Card> lastCards;
    public HashMap<String, Integer> roundsWon;
    private Deck deck;
    public String nInitiativeId;
    public int cardReference;
    public int nCards;
    public int nRounds;
    public int round;
    public int turnSuit;
    public int finalTurn;
    public int level;


    private DjamboTurn()
    {
        CardReferenceMap = fillCardMap();
    }
    public DjamboTurn(ArrayList<String> playerIds) {

        CardReferenceMap = fillCardMap();

        cardReference = -1;
        nCards = 3;
        nRounds = 1;
        level = 0;
        round = 1;
        turn = 1;
        finalTurn = nCards*playerIds.size();

        nInitiativeId = playerIds.get(0);
        deck = new Deck(false);
        deck.shuffle();
        plays = new HashMap<>();
        players = new HashMap<>();
        lastCards = new HashMap<>();
        roundsWon = new HashMap<>();
        for(int i=0; i< playerIds.size();i++)
        {
            Hand newHand = new Hand();
            Hand newPlay = new Hand();
            for(int j=0;j<nCards;j++)
            {
                newHand.addCard(deck.dealCard());
            }
            plays.put(playerIds.get(i), newPlay);
            players.put(playerIds.get(i), newHand);
            roundsWon.put(playerIds.get(i), 0);
        }
    }
    public DjamboTurn(ArrayList<String> playerIds, int _nRounds, int _nCards, int _level) {

        CardReferenceMap = fillCardMap();

        cardReference = -1;
        nCards = _nCards;
        nRounds = _nRounds;
        level = _level;
        round = 1;
        turn = 1;
        finalTurn = nCards*playerIds.size();

        nInitiativeId = playerIds.get(0);
        deck = new Deck(false);
        deck.shuffle();
        plays = new HashMap<>();
        players = new HashMap<>();
        lastCards = new HashMap<>();
        roundsWon = new HashMap<>();
        for(int i=0; i< playerIds.size();i++)
        {
            Hand newHand = new Hand();
            Hand newPlay = new Hand();
            for(int j=0;j<nCards;j++)
            {
                newHand.addCard(deck.dealCard());
            }
            plays.put(playerIds.get(i), newPlay);
            players.put(playerIds.get(i), newHand);
            roundsWon.put(playerIds.get(i), 0);
        }
    }
    public void nextRound(ArrayList<String> playerIds)
    {
        cardReference = -1;
        turn = 1;
        deck = new Deck(false);
        deck.shuffle();
        plays.clear();
        players.clear();

        for(int i=0; i< playerIds.size();i++)
        {
            Hand newHand = new Hand();
            Hand newPlay = new Hand();
            for(int j=0;j<nCards;j++)
            {
                newHand.addCard(deck.dealCard());
            }
            players.put(playerIds.get(i), newHand);
            plays.put(playerIds.get(i), newPlay);
        }
    }
    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        Gson gson = new Gson();

        try {
            retVal.put("nRounds", nRounds);
            retVal.put("nCards", nCards);
            retVal.put("level", level);
            retVal.put("round", round);
            retVal.put("turn", turn);
            retVal.put("turnSuit", turnSuit);
            retVal.put("finalTurn", finalTurn);
            retVal.put("nInitiativeId", nInitiativeId);
            retVal.put("cardReference", cardReference);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String json  = retVal.toString();
        String json2 = gson.toJson(roundsWon)+"--"+gson.toJson(lastCards)+"--"+gson.toJson(plays)+"--"+gson.toJson(players);

        String st = json + "///" + json2;

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of DjamboTurn.
    public static DjamboTurn unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new DjamboTurn();
        }

        String st;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);
        String[] parts = st.split("///");
        String[] parts2 = parts[1].split("--");
        Gson gson = new Gson();
        Type collectionType;

        collectionType= new TypeToken<HashMap<String, Integer>>(){}.getType();
        HashMap<String, Integer> roundsWon = gson.fromJson(parts2[0], collectionType);

        collectionType = new TypeToken<HashMap<String, Card>>(){}.getType();
        HashMap<String, Card> lastCards = gson.fromJson(parts2[1], collectionType);

        collectionType = new TypeToken<HashMap<String, Hand>>(){}.getType();
        HashMap<String, Hand> plays = gson.fromJson(parts2[2], collectionType);
        HashMap<String, Hand> players = gson.fromJson(parts2[3], collectionType);

        DjamboTurn retVal = new DjamboTurn();
        try {
            JSONObject obj = new JSONObject(parts[0]);

            if (obj.has("nRounds")) {
                retVal.nRounds = obj.getInt("nRounds");
            }
            if (obj.has("nCards")) {
                retVal.nCards = obj.getInt("nCards");
            }
            if (obj.has("level")) {
                retVal.level = obj.getInt("level");
            }
            if (obj.has("round")) {
                retVal.round = obj.getInt("round");
            }
            if (obj.has("turnSuit")) {
                retVal.turnSuit = obj.getInt("turnSuit");
            }
            if (obj.has("turn")) {
                retVal.turn = obj.getInt("turn");
            }
            if (obj.has("finalTurn")) {
                retVal.finalTurn = obj.getInt("finalTurn");
            }
            if (obj.has("nInitiativeId")) {
                retVal.nInitiativeId = obj.getString("nInitiativeId");
            }
            if (obj.has("cardReference")) {
                retVal.cardReference = obj.getInt("cardReference");
            }
            retVal.roundsWon = roundsWon;
            retVal.lastCards = lastCards;
            retVal.players = players;
            retVal.plays = plays;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retVal;
    }
}
