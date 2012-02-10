package dominion481.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dominion481.game.Card.Type;
import dominion481.server.Action;
import dominion481.server.ClientHandler;
import dominion481.server.RemotePlayer;

public class RemoteDominionPlayer extends DominionPlayer implements
      RemotePlayer {
   private ClientHandler client;
   private List<Action> actions = DominionAction.defaultActions;

   @Override
   public void notifyActions() {
      // TODO Auto-generated method stub

   }

   @Override
   public List<Card> cellar() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Card> chapel() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean chancellor() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Card workshop() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Card feast() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Card[] remodel() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Card throneRoom() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean libraryDiscard(Card card) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Card[] mine() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void actionPhase() {
      client.write("hand" + getCardColors(hand));
      List<Card> actionCards = Card.filter(hand, Type.ACTION);

      if (actionCards.size() == 0)
         return;

      List<Action> prev = actions;
      client.write("actionPhase", actionCards);
      actions = prev;
   }

   @Override
   public void treasurePhase() {
      List<Card> treasureCards = Card.filter(hand, Type.TREASURE);

      List<Action> prev = actions;
      actions = DominionAction.treasurePhaseActions;
      client.write("treasurePhase " + getCardColors(treasureCards));
      synchronized (client) {
         try {
            client.wait();
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }
      actions = prev;
   }

   @Override
   public void buyPhase() {
      List<Card> availableCards = new ArrayList<Card>();
      for (Card c : parentGame.boardMap.keySet())
         if (c.getCost() <= coin)
            availableCards.add(c);      

      List<Action> prev = actions;
      actions = DominionAction.buyPhaseActions;

      Collections.sort(availableCards, new Comparator<Card>() {
         @Override
         public int compare(Card a, Card b) {
            return b.getCost() - a.getCost();
         }
      });

      while (buys > 0) {
         client.write("buyPhase " + getCardColors(availableCards));
         try {
            synchronized (client) {
               client.wait();
            }
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
         for (int i = 0; i < availableCards.size(); i++) {
            if (availableCards.get(i).getCost() > coin)
               availableCards.remove(i--);
         }
      }

      actions = prev;
   }

   @Override
   public ClientHandler getClient() {
      return client;
   }

   @Override
   public List<Action> getActions() {
      return actions;
   }

   public RemoteDominionPlayer(Dominion game, ClientHandler client) {
      super(game, client.getNick());
      this.client = client;
   }

   private StringBuilder getCardColors(List<Card> cards) {
      StringBuilder sb = new StringBuilder();
      for (Card c : cards) {
         sb.append(' ');
         sb.append(c.type.colorCode);
         sb.append(c);
         sb.append("\033[0m");
      }
      return sb;
   }
}
