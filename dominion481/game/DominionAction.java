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

   static final List<Action> buyPhaseActions = Arrays.asList(new Action(
         "buyCard", "buy") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         DominionPlayer p = (DominionPlayer) client.getPlayer();
         if (args.length < 2) {
            p.buys = 0;
         } else {
            try {
               p.buy(Enum.valueOf(Card.class, args[1]));
               synchronized (client) {
                  client.notify();
               }
            } catch (IllegalArgumentException e) {
               client.write("invalidCard " + args[1]);
            }
         }
      }
   }, emptyAction);

   static final List<Action> treasurePhaseActions = Arrays.asList(new Action(
         "redeem") {
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
         synchronized (client) {
            client.notify();
         }
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
               try {
                  out.add(Enum.valueOf(Card.class, args[i]));
               } catch (IllegalArgumentException e) {
                  client.write("invalidCard " + args[i]);
               }
            }
         }
         return out;
      }
   }, emptyAction);
}
