package com.tshow.cardgameengine.abstractbase;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.tshow.R;
import com.tshow.cardgames.american8.American8Turn;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jordantankoua on 31/05/15.
 */
public abstract class GameTurn {
    public int turn;
    public static HashMap<String, Integer> CardReferenceMap;

    public static final String TAG = "EBTurn";

    protected GameTurn(){}

    public GameTurn(ArrayList<String> playerIds) {
    }

    // This is the byte array we will write out to the TBMP API.
    abstract public byte[] persist();



    public String getNextParticipantId(GoogleApiClient mGoogleApiClient, TurnBasedMatch mMatch, American8Turn mTurnData) {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                //desiredIndex = i + 1;
                desiredIndex = (i + 1) % participantIds.size();;
            }
        }
        if (desiredIndex > -1 && desiredIndex < participantIds.size()) {
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

    protected static HashMap<String, Integer> fillCardMap()
    {
        HashMap<String, Integer> CardReferenceMap = new HashMap<String, Integer>();

        CardReferenceMap.put("ca", R.drawable.ca);
        CardReferenceMap.put("c2", R.drawable.c2);
        CardReferenceMap.put("c3", R.drawable.c3);
        CardReferenceMap.put("c4", R.drawable.c4);
        CardReferenceMap.put("c5", R.drawable.c5);
        CardReferenceMap.put("c6", R.drawable.c6);
        CardReferenceMap.put("c7", R.drawable.c7);
        CardReferenceMap.put("c8", R.drawable.c8);
        CardReferenceMap.put("c9", R.drawable.c9);
        CardReferenceMap.put("c10", R.drawable.c10);
        CardReferenceMap.put("cj", R.drawable.cj);
        CardReferenceMap.put("cq", R.drawable.cq);
        CardReferenceMap.put("ck", R.drawable.ck);

        CardReferenceMap.put("da", R.drawable.da);
        CardReferenceMap.put("d2", R.drawable.d2);
        CardReferenceMap.put("d3", R.drawable.d3);
        CardReferenceMap.put("d4", R.drawable.d4);
        CardReferenceMap.put("d5", R.drawable.d5);
        CardReferenceMap.put("d6", R.drawable.d6);
        CardReferenceMap.put("d7", R.drawable.d7);
        CardReferenceMap.put("d8", R.drawable.d8);
        CardReferenceMap.put("d9", R.drawable.d9);
        CardReferenceMap.put("d10", R.drawable.d10);
        CardReferenceMap.put("dj", R.drawable.dj);
        CardReferenceMap.put("dq", R.drawable.dq);
        CardReferenceMap.put("dk", R.drawable.dk);

        CardReferenceMap.put("ha", R.drawable.ha);
        CardReferenceMap.put("h2", R.drawable.h2);
        CardReferenceMap.put("h3", R.drawable.h3);
        CardReferenceMap.put("h4", R.drawable.h4);
        CardReferenceMap.put("h5", R.drawable.h5);
        CardReferenceMap.put("h6", R.drawable.h6);
        CardReferenceMap.put("h7", R.drawable.h7);
        CardReferenceMap.put("h8", R.drawable.h8);
        CardReferenceMap.put("h9", R.drawable.h9);
        CardReferenceMap.put("h10", R.drawable.h10);
        CardReferenceMap.put("hj", R.drawable.hj);
        CardReferenceMap.put("hq", R.drawable.hq);
        CardReferenceMap.put("hk", R.drawable.hk);

        CardReferenceMap.put("sa", R.drawable.sa);
        CardReferenceMap.put("s2", R.drawable.s2);
        CardReferenceMap.put("s3", R.drawable.s3);
        CardReferenceMap.put("s4", R.drawable.s4);
        CardReferenceMap.put("s5", R.drawable.s5);
        CardReferenceMap.put("s6", R.drawable.s6);
        CardReferenceMap.put("s7", R.drawable.s7);
        CardReferenceMap.put("s8", R.drawable.s8);
        CardReferenceMap.put("s9", R.drawable.s9);
        CardReferenceMap.put("s10", R.drawable.s10);
        CardReferenceMap.put("sj", R.drawable.sj);
        CardReferenceMap.put("sq", R.drawable.sq);
        CardReferenceMap.put("sk", R.drawable.sk);

        CardReferenceMap.put("sjo", R.drawable.sjo);
        CardReferenceMap.put("hjo", R.drawable.hjo);

        return CardReferenceMap;
    }
}
