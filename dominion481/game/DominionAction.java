package dominion481.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dominion481.server.Action;
import dominion481.server.ClientHandler;

public class DominionAction {
   private static final Action emptyAction = new Action("") {
      @Override
      public void handle(String[] args, ClientHandler client) {
      }
   };
   
   private static final Action passAction = new Action("pass") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         synchronized (client) { client.notify(); }
      }
   };
   
   private static final Action chooseCardAction = new Action("[cardName]") {
      public void handle(String[] args, ClientHandler client) {
         RemoteDominionPlayer p = (RemoteDominionPlayer)client.getPlayer();
         
         Card c = Card.getCard(args[0]);
         if (c == null) {
            client.write("invalidCard "+args[0]);
            return;
         }
         
         p.setRet(c);
         /*p.ret = c;
         synchronized (client) { client.notify(); }*/
      }
      
      public boolean matches(String s) {
         return Card.getCard(s) != null;
      }
   };
   
   private static final Action chooseCardsAction = new Action("[cardName...]") {
      public void handle(String[] args, ClientHandler client) {
         RemoteDominionPlayer p = (RemoteDominionPlayer)client.getPlayer();
         List<Card> out = new ArrayList<Card>(args.length);
         
         for (int i = 0; i < args.length; i++) {
            Card c = Card.getCard(args[i]);
            if (c == null)
               client.write("invalidCard " + args[i]);
            else
               out.add(c);
         }
         
         p.setRet(out);
         /*p.ret = out;
         synchronized (client) { client.notify(); }*/
      }
      
      public boolean matches(String s) {
         return Card.getCard(s) != null;
      }
   };

   static final List<Action> defaultActions = Arrays.asList(new Action(
         "getStatus", "status") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         Dominion game = (Dominion) client.getGame();
         List<DominionPlayer> players = game.players;

         for (DominionPlayer player : players) {
            System.out.println(" --- " + player.nick + " --- ");
            System.out.println("Hand: " + player.hand);
            System.out.println("Discard: " + player.discard);
            System.out.println("Deck: " + player.deck);
            System.out.println("In Play: " + player.inPlay);
            System.out.println("Actions: " + player.actions);
            System.out.println("Coins: " + player.coin);
            System.out.println("Buys: " + player.buys);
            System.out.println();
         }
         System.out.println(" --- Board --- ");
         System.out.println("Cards on the table: " + game.getBoardMap());
         System.out.println("Current turn: " + game.currentTurn.nick);
      };
   }, emptyAction);

   static final List<Action> treasurePhaseActions = Arrays.asList(new Action(
         "redeem", "r") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         DominionPlayer player = (DominionPlayer) client.getPlayer();
         List<Card> toRedeem = getRedemptionCards(args, player, client);
         
         for (Card c : toRedeem) {
            try {
               player.playTreasure(c);
            } catch (IllegalStateException e) {
               client.write("invalidCard " + c);
            }
         }
         synchronized (client) { client.notify(); }
      }

      private List<Card> getRedemptionCards(String[] args,
            DominionPlayer player, ClientHandler client) {
         List<Card> out = new ArrayList<Card>(args.length);
         if (args.length == 1) {
            for (Card c : player.hand)
               if (c.type == Card.Type.TREASURE)
                  out.add(c);
         } else {
            for (int i = 1; i < args.length; i++) {
               Card c = Card.getCard(args[i]);
               if (c == null) {
                  client.write("invalidCard " + args[i]);
               } else {
                  out.add(c);
               }
            }
         }
         return out;
      }
   }, passAction);
   
   static final List<Action> buyPhaseActions = Arrays.asList(new Action(
         "buyCard", "buy", "b") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         DominionPlayer p = (DominionPlayer) client.getPlayer();
         if (args.length < 2) {
            p.buys = 0;
         } else {
            Card c = Card.getCard(args[1]);
            if (c == null) {
               client.write("invalidCard " + args[1]);
            } else {
               try {
                  p.buy(c);
               } catch (IllegalArgumentException e) {
                  client.write(e.getMessage());
               }
            }
         }
         synchronized (client) { client.notify(); }
      }
   }, passAction);
   
   static final List<Action> actionPhaseActions = Arrays.asList(chooseCardAction, passAction);
   
   static final List<Action> yesNoActions = Arrays.asList(new Action("yes") {
      public void handle(String[] args, ClientHandler client) {
         ((RemoteDominionPlayer)client.getPlayer()).ret = true;
         synchronized (client) { client.notify(); }
      }
   }, new Action("no") {
      public void handle(String[] args, ClientHandler client) {
         ((RemoteDominionPlayer)client.getPlayer()).ret = false;
         synchronized (client) { client.notify(); }
      }
   });
   
   static final List<Action> passableCardSelectionActions = Arrays.asList(chooseCardAction, passAction);
   
   static final List<Action> cardSelectionActions = Arrays.asList(chooseCardAction, emptyAction);
   
   static final List<Action> cardsSelectionActions = Arrays.asList(chooseCardsAction, emptyAction);
}
