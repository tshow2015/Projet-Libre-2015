package com.tshow.cardgameengine.cardelement;
/**
 * An object of type Hand represents a hand of cards.  The
 * cards belong to the class Card.  A hand is empty when it
 * is created, and any number of cards can be added to it.
 */

import java.util.ArrayList;

public class Hand {

    private ArrayList<com.tshow.cardgameengine.cardelement.Card> hand;   // The cards in the hand.

    /**
     * Create a hand that is initially empty.
     */
    public Hand() {
        hand = new ArrayList<>();
    }

    /**
     * Remove all cards from the hand, leaving it empty.
     */
    public void clear() {
        hand.clear();
    }

    /**
     * Add a card to the hand.  It is added at the end of the current hand.
     * @param c the non-null card to be added.
     * @throws NullPointerException if the parameter c is null.
     */
    public void addCard(Card c) {
        if (c == null)
            throw new NullPointerException("Can't add a null card to a hand.");
        hand.add(c);
    }

    /**
     * Remove a card from the hand, if present.
     * @param c the card to be removed.  If c is null or if the card is not in 
     * the hand, then nothing is done.
     */
    public void removeCard(Card c) {
        hand.remove(c);
    }

    /**
     * Remove the card in a specified position from the hand.
     * @param position the position of the card that is to be removed, where
     * positions are starting from zero.
     * @throws IllegalArgumentException if the position does not exist in
     * the hand, that is if the position is less than 0 or greater than
     * or equal to the number of cards in the hand.
     */
    public void removeCard(int position) {
        if (position < 0 || position >= hand.size())
            throw new IllegalArgumentException("Position does not exist in hand: "
                    + position);
        hand.remove(position);
    }

    public boolean findSuit(int aSuit)
    {
        for(int i=0; i<hand.size(); i++)
        {
            if(hand.get(i).getSuit() == aSuit) return true;
        }
        return false;
    }

    /**
     * Returns the number of cards in the hand.
     */
    public int getCardCount() {
        return hand.size();
    }

    /**
     * Gets the card in a specified position in the hand.  (Note that this card
     * is not removed from the hand!)
     * @param position the position of the card that is to be returned
     * @throws IllegalArgumentException if position does not exist in the hand
     */
    public Card getCard(int position) {
        if (position < 0 || position >= hand.size())
            throw new IllegalArgumentException("Position does not exist in hand: "
                    + position);
        return hand.get(position);
    }

    /**
     * Sorts the cards in the hand so that cards of the same suit are
     * grouped together, and within a suit the cards are sorted by value.
     * Note that aces are considered to have the lowest value, 1.
     */
    public void sortBySuit() {
        ArrayList<Card> newHand = new ArrayList<>();
        while (hand.size() > 0) {
            int pos = 0;  // Position of minimal card.
            Card c = hand.get(0);  // Minimal card.
            for (int i = 1; i < hand.size(); i++) {
                Card c1 = hand.get(i);
                if ( c1.getSuit() < c.getSuit() ||
                        (c1.getSuit() == c.getSuit() && c1.getValue() < c.getValue()) ) {
                    pos = i;
                    c = c1;
                }
            }
            hand.remove(pos);
            newHand.add(c);
        }
        hand = newHand;
    }

    /**
     * Sorts the cards in the hand so that cards of the same value are
     * grouped together.  Cards with the same value are sorted by suit.
     * Note that aces are considered to have the lowest value, 1.
     */
    public void sortByValue() {
        ArrayList<Card> newHand = new ArrayList<>();
        while (hand.size() > 0) {
            int pos = 0;  // Position of minimal card.
            Card c = hand.get(0);  // Minimal card.
            for (int i = 1; i < hand.size(); i++) {
                Card c1 = hand.get(i);
                if ( c1.getValue() < c.getValue() ||
                        (c1.getValue() == c.getValue() && c1.getSuit() < c.getSuit()) ) {
                    pos = i;
                    c = c1;
                }
            }
            hand.remove(pos);
            newHand.add(c);
        }
        hand = newHand;
    }
    public void sortByProb() {
        ArrayList<Card> newHand = new ArrayList<>();
        while (hand.size() > 0) {
            int pos = 0;  // Position of minimal card.
            Card c = hand.get(0);  // Minimal card.
            for (int i = 1; i < hand.size(); i++) {
                Card c1 = hand.get(i);
                if ( c1.getProbability() < c.getProbability() ||
                        (c1.getProbability() == c.getProbability() && c1.getSuit() < c.getSuit()) ) {
                    pos = i;
                    c = c1;
                }
            }
            hand.remove(pos);
            newHand.add(c);
        }
        hand = newHand;
    }
    /**
     * Sorts the cards in the hand so that cards of the same value are
     * grouped together.  Cards with the same value are sorted by suit.
     * Note that aces are considered to have the lowest value, 1.
     */
    public String printHand() {
    	String res = "";
    	for (int i = 0; i< hand.size(); i++)
    	{
            //System.out.println(i + " :  " + hand.get(i));
            res+= i + " :  " + hand.get(i)+"\n";
    	}
    	return res;
    }
    public boolean hasCardValue(int value)
    {
        for(int i=0;i<hand.size();i++)
        {
            if(hand.get(i).getValue()==value)
                return true;
        }
        return false;
    }
    public boolean hasCardValue(int value, int suitToPlay)
    {

            for(int i=0;i<hand.size();i++)
            {
                if(hand.get(i).getValue()==value && hand.get(i).getSuit()==suitToPlay )
                    return true;
            }

        return false;
    }

    public boolean hasCardSuit(int suit)
    {
        for(int i=0;i<hand.size();i++)
        {
            if(hand.get(i).getSuit()==suit)
                return true;
        }
        return false;
    }
    public Card getCardbyValue(int value)
    {
        for(int i=0;i<hand.size();i++)
        {
            if(hand.get(i).getValue()==value)
                return hand.get(i);
        }
        return null;
    }
    public Card getCardbyValueSuit(int value, int suit)
    {
        for(int i=0;i<hand.size();i++)
        {
            if(hand.get(i).getValue()==value)
                return hand.get(i);
        }
        return null;
    }
    public Card getMatchingCard(int value, int suitToPlay)
    {
        for(int i=0;i<hand.size();i++)
        {
            if(hand.get(i).getValue()==value && hand.get(i).getSuit()==suitToPlay)
                return hand.get(i);
        }
        return null;
    }
    public ArrayList<Card> getHand() {return hand;}
    public int mostCommonSuit()
    {
        int[] suitsArray = new int[4];
        int maxSuit=0;
        for(int i =0; i<hand.size();i++)
        {
            if(hand.get(i).getValue()<14)
                suitsArray[hand.get(i).getSuit()]+=1;
        }
        for(int i =0; i<4;i++)
        {
            if(suitsArray[i]>maxSuit) maxSuit = suitsArray[i];
        }
        return maxSuit;
    }
    public int findReference(Card card)
    {
        for(int i=0;i<hand.size();i++)
        {
            if(card==hand.get(i))return i;
        }
        return -1;
    }
}



