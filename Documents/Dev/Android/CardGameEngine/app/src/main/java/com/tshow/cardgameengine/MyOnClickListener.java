package com.tshow.cardgameengine;
import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.tshow.cardgameengine.cardelement.Hand;

public class MyOnClickListener implements View.OnClickListener
{
  //public MyOnClickListener(GoogleApiClient mGoogleApiClient, TurnBasedMatch mMatch, GameTurn mTurnData, int i, Hand playerHand, TextView t, Activity context, HashMap<String, Integer> CardReferenceMap) {
    public MyOnClickListener(int i,Hand playerHand, TextView t, Activity context, HashMap<String, Integer> CardReferenceMap) {
      int i1 = i;
      Hand playerHand1 = playerHand;
      TextView t1 = t;
      Activity context1 = context;
      HashMap<String, Integer> cardReferenceMap = CardReferenceMap;
  }

  @Override
  public void onClick(View v){}

};