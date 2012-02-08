package dominion481.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dominion481.game.Card.Type;

public abstract class Player {
   List<Card> hand;
   LinkedList<Card> deck;
   List<Card> discard;
   List<Card> inPlay;
   
   int actions;
   int buys;
   int coin;
   
   DominionState parentGame;
   
   /**
    * Draws a card from the player's deck into hand
    * @return The drawn card
    */
   final Card draw() {
      if (deck.isEmpty()) {
         /* TODO Shuffle deck */
      }
      
      //It's possible, though rare, to have empty deck and discard
      if (deck.isEmpty()) { 
         return null;
      }
      
      Card toDraw = deck.poll();
      hand.add(toDraw);
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
      if (actions < 1) {
         throw new IllegalStateException("An action cannot be played when no actions remain");
      }
      
      if (card.type != Type.ACTION) {
         throw new IllegalArgumentException("Card played is not an action");
      }
      
      if (!hand.remove(card)) {
         throw new IllegalArgumentException("Card is not in hand!");
      }
      
      inPlay.add(card);
      actions -= 1;

      card.play(this, parentGame);
   }
   
   public final void playAction(Card card) {
      if (actions < 1) {
         throw new IllegalStateException("An action cannot be played when no actions remain");
      }
      
      if (card.type != Type.ACTION) {
         throw new IllegalArgumentException("Card played is not an action");
      }
      
      if (!hand.remove(card)) {
         throw new IllegalArgumentException("Card is not in hand!");
      }
      
      inPlay.add(card);
      actions -= 1;

      card.play(this, parentGame);
   }

   protected final long upi;
   public abstract void takeTurn();
   public abstract void notifyActions();
   public abstract int getVictoryPoints();
   
   //Card Behaviors
   public abstract List<Card> cellar();
   public abstract List<Card> chapel();
   public abstract boolean chancellor();
   public abstract Card workshop();
   public abstract Card feast();
   public abstract Card[] remodel();
   public abstract Card throneRoom();
   public abstract boolean libraryDiscard(Card card);
   public abstract Card[] mine();

   public Player(DominionState state) {
      this.parentGame = state;
   }
}
