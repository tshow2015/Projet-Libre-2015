package com.tshow.cardgameengine;
import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class MyOnClickListener implements View.OnClickListener
{

  //public MyOnClickListener(GoogleApiClient mGoogleApiClient, TurnBasedMatch mMatch, GameTurn mTurnData, int i, Hand playerHand, TextView t, Activity context, HashMap<String, Integer> CardReferenceMap) {
    public MyOnClickListener(int i, Hand playerHand, TextView t, Activity context, HashMap<String, Integer> CardReferenceMap) {
      //this.mGoogleApiClient = mGoogleApiClient;
      //this.mMatch = mMatch;
      //this.mTurnData = mTurnData;
      int i1 = i;
      Hand playerHand1 = playerHand;
      TextView t1 = t;
      Activity context1 = context;
      HashMap<String, Integer> cardReferenceMap = CardReferenceMap;
  }

  @Override
  public void onClick(View v)
  {
      //read your lovely variable
  }

};