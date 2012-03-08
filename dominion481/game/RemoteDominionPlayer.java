package dominion481.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dominion481.game.Card.Type;
import dominion481.server.Action;
import dominion481.server.ClientHandler;
import dominion481.server.RemotePlayer;

import static dominion481.game.DominionAction.*;

public class RemoteDominionPlayer extends DominionPlayer implements
      RemotePlayer {
   private ClientHandler client;
   private List<Action> actions = defaultActions;
   Object ret;
   
   private void waitOnClient() {
      synchronized (client) {
         try {
            client.wait();
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }
   }
   
   private void getResponse(List<Action> actions, String prompt) {
      List<Action> prev = this.actions;
      this.actions = actions;
      
      client.write(prompt);
      waitOnClient();
      
      this.actions = prev;
   }
   
   private void getResponse(List<Action> actions, String prompt, List<?> objs) {
      List<Action> prev = this.actions;
      this.actions = actions;
      
      client.write(prompt, objs);
      waitOnClient();
      
      this.actions = prev;
   }
   
   private Object getRet() {
      Object out = ret;
      ret = null;
      return out;
   }

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
      Card[] out = new Card[2];
      getResponse(cardSelectionActions, "discard", hand);
      out[0] = (Card)getRet();
      getResponse(cardSelectionActions, "gain", hand);
      out[1] = (Card)getRet();
      return out;
   }

   @Override
   public Card throneRoom() {
      List<Card> actionCards = Card.filter(hand, Type.ACTION);

      if (actionCards.size() == 0)
         return null;

      getResponse(passableCardSelectionActions, "throneRoom", actionCards);

      return (Card)getRet();
   }

   @Override
   public boolean libraryDiscard(Card card) {
      client.getGame().notifyAll("cardReveal "+client.getPlayer()+" "+card);

      getResponse(yesNoActions, "keep? "+card);

      return !(Boolean)getRet();
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

      getResponse(actionPhaseActions, "actionPhase", actionCards);
      
      Card c = (Card)getRet();
      if (c != null)
         playAction(c);
   }

   @Override
   public void treasurePhase() {
      List<Card> treasureCards = Card.filter(hand, Type.TREASURE);
      
      getResponse(treasurePhaseActions, "treasurePhase " + getCardColors(treasureCards));
   }

   @Override
   public void buyPhase() {
      List<Card> availableCards = new ArrayList<Card>();
      for (Card c : parentGame.boardMap.keySet())
         if (c.getCost() <= coin)
            availableCards.add(c);      

      List<Action> prev = actions;
      actions = buyPhaseActions;

      Collections.sort(availableCards, new Comparator<Card>() {
         @Override
         public int compare(Card a, Card b) {
            return b.getCost() - a.getCost();
         }
      });

      while (buys > 0) {
         client.write("buyPhase " + getCardColors(availableCards));
         
         waitOnClient();
         for (int i = 0; i < availableCards.size(); i++)
            if (availableCards.get(i).getCost() > coin)
               availableCards.remove(i--);
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
         sb.append(c.getColorName());
      }
      return sb;
   }
   
   public String toString() {
      return client.getNick();
   }
}
