package dominion481.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dominion481.game.Card.Type;
import dominion481.game.DominionState.Phase;

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
   }
   
   public final void buy(Card card) {
      if (coin < card.getCost()) {
         throw new IllegalArgumentException("Not enough coin to purchase " + card);
      }
      
      if (!gain(card)) {
         throw new IllegalArgumentException(card + " is not available for purchase");
      }
      
      coin -= card.getCost();
   }
   
   final void endTurn() {
      discard.addAll(hand);
      discard.addAll(inPlay);
      prepareTurn();
   }

   final void prepareTurn() {
      hand = new ArrayList<Card>();
      inPlay = new ArrayList<Card>();
      actions = 1;
      coin = 0;
      buys = 1;
      
      for (int i = 0; i < 5; i++) {
         draw();
      }
   }
   
   public abstract void takeTurn();
   public abstract void playTreasure();
   public abstract void buyCards();
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
