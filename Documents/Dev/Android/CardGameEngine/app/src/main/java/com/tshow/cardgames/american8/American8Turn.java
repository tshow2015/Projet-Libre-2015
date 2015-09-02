package com.tshow.cardgames.american8;

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

public class American8Turn extends GameTurn {

    private static final String TAG = "EBTurn";

    static ArrayList<Integer> afterAceJoker;
    static ArrayList<Integer> passePartouts;
    public static ArrayList<Integer> specialCards;
    static HashMap<String, Integer> CardReferenceMap;

    private String data;
    public String winnerGame;
    public int nCards;
    public int cardReference;
    public int account;
    public int commandSuit;
    public int level;
    public Deck deck;
    //public Deck deck1;
    public Card currentCard;
    private Card cardToPlay;
    public boolean cardConsidered;
    public boolean cardAccepted;
    public boolean adderCard;
    public boolean winner;
    public boolean skipNext;
    public boolean incOrder;
    public HashMap<String, Hand> players;

    private American8Turn()
    {
        specialCards = new ArrayList<>();
        specialCards.add(Card.ACE);
        specialCards.add(2);
        specialCards.add(7);
        specialCards.add(8);
        specialCards.add(10);
        specialCards.add(Card.JOKER);

        afterAceJoker = new ArrayList<>();
        afterAceJoker.add(Card.ACE);
        afterAceJoker.add(8);
        afterAceJoker.add(Card.JOKER);

        passePartouts = new ArrayList<>();
        passePartouts.add(Card.JOKER);
        passePartouts.add(8);
        passePartouts.add(2);

        CardReferenceMap = fillCardMap();
    }
    public American8Turn(ArrayList<String> playerIds) {

        specialCards = new ArrayList<>();
        specialCards.add(Card.ACE);
        specialCards.add(2);
        specialCards.add(7);
        specialCards.add(8);
        specialCards.add(10);
        specialCards.add(Card.JOKER);

        afterAceJoker = new ArrayList<>();
        afterAceJoker.add(Card.ACE);
        afterAceJoker.add(8);
        afterAceJoker.add(Card.JOKER);

        passePartouts = new ArrayList<>();
        passePartouts.add(Card.JOKER);
        passePartouts.add(8);
        passePartouts.add(2);

        CardReferenceMap = fillCardMap();


        cardConsidered = false;
        turn = 1;
        winner = false;
        cardReference = -1;
        account = 0;
        commandSuit=-1;
        nCards = 2;
        level = 0;
        skipNext = false;
        incOrder = true;
        adderCard = false;

        deck = new Deck(true);
        deck.shuffle();

        while(specialCards.contains(deck.peekCard().getValue()))
        {
            deck.shuffle();
        }
        currentCard = deck.dealCard();  // The current card, which the user sees.

        players = new HashMap<>();
        for(int i=0; i< playerIds.size();i++)
        {
            Hand newHand = new Hand();
            for(int j=0;j<nCards;j++)
            {
                newHand.addCard(deck.dealCard());
            }
            players.put(playerIds.get(i), newHand);
        }

    }
    public American8Turn(ArrayList<String> playerIds, int _nCards, int _level) {

        specialCards = new ArrayList<>();
        specialCards.add(Card.ACE);
        specialCards.add(2);
        specialCards.add(7);
        specialCards.add(8);
        specialCards.add(10);
        specialCards.add(Card.JOKER);

        afterAceJoker = new ArrayList<>();
        afterAceJoker.add(Card.ACE);
        afterAceJoker.add(8);
        afterAceJoker.add(Card.JOKER);

        passePartouts = new ArrayList<>();
        passePartouts.add(Card.JOKER);
        passePartouts.add(8);
        passePartouts.add(2);

        CardReferenceMap = fillCardMap();


        cardConsidered = false;
        turn = 1;
        winner = false;
        cardReference = -1;
        account = 0;
        commandSuit=-1;
        nCards = _nCards;
        level = _level;
        skipNext = false;
        incOrder = true;
        adderCard = false;

        deck = new Deck(true);
        deck.shuffle();

        while(specialCards.contains(deck.peekCard().getValue()))
        {
            deck.shuffle();
        }
        currentCard = deck.dealCard();  // The current card, which the user sees.

        players = new HashMap<>();
        for(int i=0; i< playerIds.size();i++)
        {
            Hand newHand = new Hand();
            for(int j=0;j<nCards;j++)
            {
                newHand.addCard(deck.dealCard());
            }
            players.put(playerIds.get(i), newHand);
        }

    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        Gson gson = new Gson();

        try {
            retVal.put("data", data);
            retVal.put("turn", turn);
            retVal.put("level", level);
            retVal.put("cardReference", cardReference);
            retVal.put("account", account);
            retVal.put("commandSuit", commandSuit);
            retVal.put("cardConsidered", cardConsidered);
            retVal.put("cardAccepted", cardAccepted);
            retVal.put("winner", winner);
            retVal.put("nCards", nCards);
            retVal.put("skipNext", skipNext);
            retVal.put("incOrder", incOrder);
            retVal.put("adderCard", adderCard);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String json  = retVal.toString();
        String json2 = gson.toJson(currentCard)+"--"+gson.toJson(cardToPlay)+"--"+gson.toJson(players)+"--"+gson.toJson(deck);

        String st = json + "///" + json2;

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of American8Turn.
    public static American8Turn unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new American8Turn();
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

        Card currentCard = gson.fromJson(parts2[0], Card.class);
        Card cardToPlay = gson.fromJson(parts2[1], Card.class);
        Type collectionType = new TypeToken<HashMap<String, Hand>>(){}.getType();
            HashMap<String, Hand> players = gson.fromJson(parts2[2], collectionType);
        Deck deck = gson.fromJson(parts2[3], Deck.class);

        American8Turn retVal = new American8Turn();
        try {
            JSONObject obj = new JSONObject(parts[0]);

            if (obj.has("data")) {
                retVal.data = obj.getString("data");
            }
            if (obj.has("turn")) {
                retVal.turn = obj.getInt("turn");
            }
            if (obj.has("level")) {
                retVal.level = obj.getInt("level");
            }
            if (obj.has("cardReference")) {
                retVal.cardReference = obj.getInt("cardReference");
            }
            if (obj.has("account")) {
                retVal.account = obj.getInt("account");
            }

            if (obj.has("commandSuit")) {
                retVal.commandSuit = obj.getInt("commandSuit");
            }
            if (obj.has("nCards")) {
                retVal.nCards = obj.getInt("nCards");
            }
            if (obj.has("cardConsidered")) {
                retVal.cardConsidered = obj.getBoolean("cardConsidered");
            }
            if (obj.has("cardAccepted")) {
                retVal.cardAccepted = obj.getBoolean("cardAccepted");
            }
            if (obj.has("incOrder")) {
                retVal.incOrder= obj.getBoolean("incOrder");
            }
            if (obj.has("skipNext")) {
                retVal.skipNext = obj.getBoolean("skipNext");
            }
            if (obj.has("winner")) {
                retVal.winner = obj.getBoolean("winner");
            }
            if (obj.has("adderCard")) {
                retVal.adderCard = obj.getBoolean("adderCard");
            }

            retVal.currentCard = currentCard;
            retVal.cardToPlay = cardToPlay;

            retVal.players = players;

            retVal.deck = deck;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }

}
