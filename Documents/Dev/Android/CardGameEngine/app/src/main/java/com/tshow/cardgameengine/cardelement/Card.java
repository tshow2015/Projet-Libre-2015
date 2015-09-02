package com.tshow.cardgameengine.cardelement;
/**
 * An object of type Card represents a playing card from a
 * standard Poker deck, including Jokers.  The card has a suit, which
 * can be spades, hearts, diamonds, clubs, or joker.  A spade, heart,
 * diamond, or club has one of the 14 values: ace, 2, 3, 4, 5, 6, 7,
 * 8, 9, 10, jack, queen, king or joker.  Note that "ace" is considered to be
 * the smallest value.
 */
public class Card {
   
   private final static int SPADES = 0;   // Codes for the 4 suits, plus Joker.
   private final static int HEARTS = 1;
   private final static int CLUBS = 2;
   private final static int DIAMONDS = 3;

   public final static int ACE = 1;      // Codes for the non-numeric cards.
   public final static int JACK = 11;    //   Cards 2 through 10 have their 
   public final static int QUEEN = 12;   //   numerical values for their codes.
   public final static int KING = 13;
   public final static int JOKER = 14;
   
   /**
    * This card's suit, one of the constants SPADES, HEARTS, DIAMONDS,
    * or CLUBS.  The suit cannot be changed after the card is
    * constructed.
    */
   private final int suit;
   private double associatedProbability;
   public void setProbability(double probability){associatedProbability=probability;}
   public double getProbability(){return associatedProbability;}

   /**
    * The card's value.  For a normal card, this is one of the values
    * 1 through 14, with 1 representing ACE.  The value cannot be changed after the card
    * is constructed.
    */
   private final int value;
   
   /**
    * Creates a Joker, with 1 as the associated value.  (Note that
    * "new Card()" is equivalent to "new Card(1,Card.JOKER)".)
    */
   public Card(int colour) {
      suit = colour%2;
      value = JOKER;
   }
   
   /**
    * Creates a card with a specified suit and value.
    * @param theValue the value of the new card.  For a regular card (non-joker),
    * the value must be in the range 1 through 13, with 1 representing an Ace.
    * You can use the constants Card.ACE, Card.JACK, Card.QUEEN, and Card.KING.  
    * @param theSuit the suit of the new card.  This must be one of the values
    * Card.SPADES, Card.HEARTS, Card.DIAMONDS, Card.CLUBS, or Card.JOKER.
    * @throws IllegalArgumentException if the parameter values are not in the
    * permissible ranges
    */
   public Card(int theValue, int theSuit) {
      if (theSuit != SPADES && theSuit != HEARTS && theSuit != DIAMONDS && 
            theSuit != CLUBS)
         throw new IllegalArgumentException("Illegal playing card suit");
      if (theValue < 1 || theValue > 14)
         throw new IllegalArgumentException("Illegal playing card value");
      value = theValue;
      suit = theSuit;
   }

   /**
    * Returns the suit of this card.
    * @returns the suit, which is one of the constants Card.SPADES, 
    * Card.HEARTS, Card.DIAMONDS, or Card.CLUBS
    */
   public int getSuit() {
      return suit;
   }
   
   /**
    * Returns the value of this card.
    * @return the value, which is one of the numbers 1 through 14
    */
   public int getValue() {
      return value;
   }
   
   /**
    * Returns a String representation of the card's suit.
    * @return one of the strings "Spades", "Hearts", "Diamonds", "Clubs".
    */
   private String getSuitAsString() {
      switch ( suit ) {
      case SPADES:   return "Spades";
      case HEARTS:   return "Hearts";
      case DIAMONDS: return "Diamonds";
      case CLUBS:    return "Clubs";
      default:       return "Spades";
      }
   }
   
   /**
    * Returns a String representation of the card's value.
    * @return for a regular card, one of the strings "Ace", "2",
    * "3", ..., "10", "Jack", "Queen", or "King".  For a Joker, the 
    * string is always numerical.
    */
   //aNDROID, string.xml lookup
   private String getValueAsString() {
      if (suit == JOKER)
         return "" + value;
      else {
         switch ( value ) {
         case 1:   return "Ace";
         case 2:   return "2";
         case 3:   return "3";
         case 4:   return "4";
         case 5:   return "5";
         case 6:   return "6";
         case 7:   return "7";
         case 8:   return "8";
         case 9:   return "9";
         case 10:  return "10";
         case 11:  return "Jack";
         case 12:  return "Queen";
         case 13:  return "King";
         default:  return "Joker";
         }
      }
   }
   public String getResourceString() 
   {
	    String charSuit, charValue;

	    switch(suit)
		{
			case 0: charSuit = "s";
					break;
			case 1: charSuit = "h";
					break;
			case 2: charSuit = "c";
					break;
			case 3: charSuit = "d";
					break;
			default: charSuit = "s";
		}
		switch(value)
		{
			case 1: charValue = "a";
					break;
			case 11: charValue = "j";
					break;
			case 12: charValue = "q";
					break;
			case 13: charValue = "k";
					break;			
			case 14: charValue = "jo";
					break;			
			default: charValue = Integer.toString(value);
					break;
		}    	
		return charSuit+charValue;
   }

   /**
    * Returns a string representation of this card, including both
    * its suit and its value (except that for a Joker with value 1,
    * the return value is just "Joker").  Sample return values
    * are: "Queen of Hearts", "10 of Diamonds", "Ace of Spades",
    * "Joker", "Joker #2"
    */
   public String toString() {
      if (value == JOKER) {
         if (suit%2 == 0)
            return "Black Joker";
         else
            return "Red Jocker";
      }
      else
         return getValueAsString() + " of " + getSuitAsString();
   }
   

} // end class Card
