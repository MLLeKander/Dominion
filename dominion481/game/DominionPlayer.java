package dominion481.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dominion481.game.Card.Type;
import dominion481.game.Dominion.Phase;

public abstract class DominionPlayer {
   List<Card> hand = new ArrayList<Card>();
   LinkedList<Card> deck = new LinkedList<Card>();
   List<Card> discard = new ArrayList<Card>();
   List<Card> inPlay = new ArrayList<Card>();
   
   String nick;
   
   int actions;
   int buys;
   int coin;
   
   Dominion parentGame;
   
   /**
    * Draws a card from the player's deck into hand
    * @return The drawn card
    */
   final Card draw() {
      if (deck.isEmpty()) {
         while (discard.size() > 0) {
            int ndx = (int)Math.floor(Math.random() * discard.size());
            deck.add(discard.remove(ndx));
         }
      }
      
      //It's possible, though rare, to have empty deck and discard
      if (deck.isEmpty()) { 
         return null;
      }
      
      Card toDraw = deck.poll();
      hand.add(toDraw);
      Collections.sort(hand);
      return toDraw;
   }
   
   final void discard(Card card) {
      if (!hand.remove(card)) {
         throw new IllegalArgumentException("Discard " + card + " is not in hand");
      }
      
      discard.add(card);
   }
   
   final boolean gain(Card card) {
      Map<Card, Integer> board = parentGame.boardMap;
      
      if (!board.containsKey(card)) {
         throw new IllegalArgumentException("Gain " + card + " is not on the board");
      }
      
      if (board.get(card) < 1) {
         return false;
      }
      
      board.put(card, board.get(card) - 1);
      discard.add(card);
      return true;
   }
   
   public final void playAction(Card card) {
      if (parentGame.currentPhase != Phase.ACTION) {
         throw new IllegalStateException("Actions can only be played in the action phase");
      }
      
      if (parentGame.currentTurn != this) {
         throw new IllegalStateException("Cards can only be played by the current player");
      }
      
      if (actions < 1) {
         throw new IllegalStateException("An action cannot be played when no actions remain");
      }
      
      if (card.type != Type.ACTION) {
         throw new IllegalArgumentException("Card played is not an action");
      }
      
      if (!hand.remove(card)) {
         throw new IllegalArgumentException("Card " + card + " is not in hand!");
      }
      
      inPlay.add(card);
      actions -= 1;

      card.play(this, parentGame);
   }
   
   public final void playTreasure(Card card) {
      if (parentGame.currentPhase != Phase.TREASURE) {
         throw new IllegalStateException("Treasures can only be played in the treasure phase");
      }
      
      if (parentGame.currentTurn != this) {
         throw new IllegalStateException("Cards can only be played by the current player");
      }
      
      if (card.type != Type.TREASURE) {
         throw new IllegalArgumentException("Card played is not a treasure");
      }
      
      if (!hand.remove(card)) {
         throw new IllegalArgumentException("Card " + card + " is not in hand!");
      }
      
      inPlay.add(card);
      coin += card.getTreasureValue();
      parentGame.notifyAll(nick + " redeemed " + card);
   }
   
   public final void buy(Card card) {
      if (parentGame.currentPhase != Phase.BUY) {
         throw new IllegalStateException("Purchased can only be done in the buy phase");
      }
      
      if (coin < card.getCost()) {
         throw new IllegalArgumentException("Not enough coin to purchase " + card);
      }
      
      if (!gain(card)) {
         throw new IllegalArgumentException(card + " is not available for purchase");
      }
      
      coin -= card.getCost();
      buys--;
      parentGame.notifyAll("purchasedCard " + nick + " " + card);
   }
   
   final void endTurn() {
      discard.addAll(hand);
      discard.addAll(inPlay);
      hand.clear();
      inPlay.clear();
      prepareTurn();
   }

   /* 
    * Should be called before the first turn of the game, and subsequently
    * called automatically by endTurn
    */
   final void prepareTurn() {
      //hand = new ArrayList<Card>();
      //inPlay = new ArrayList<Card>();
      actions = 1;
      coin = 0;
      buys = 1;
      
      for (int i = 0; i < 5; i++) {
         draw();
      }
   }
   
   final int getVictoryPoints() { 
      int sum = 0;
      for (Card c : discard)
         sum += c.getVp(this);
      for (Card c : deck)
         sum += c.getVp(this);
      for (Card c : hand)
         sum += c.getVp(this);
      
      // TODO is this one needed?      
      for (Card c : inPlay)
         sum += c.getVp(this);
    
      return sum;
   }
   
   public void notify(String s) {};

   public abstract void actionPhase();
   public abstract void treasurePhase();
   public abstract void buyPhase();
   
   //Card Behaviors
   public abstract List<Card> cellar();
   public abstract List<Card> chapel();
   public abstract boolean chancellor();
   public abstract Card workshop();
   public abstract Card feast();
   public abstract Card[] remodel();
   public abstract Card throneRoom();
   public abstract boolean libraryDiscard(Card card);
   public abstract Card bureaucrat();
   public abstract List<Card> militia();
   public abstract boolean spyDiscard(Card c, DominionPlayer p);
   public abstract boolean theifGain(Card toTrash);
   public abstract Card[] mine();

   public DominionPlayer(Dominion state, String nick) {
      this.parentGame = state;
      this.nick = nick;
   }


}
