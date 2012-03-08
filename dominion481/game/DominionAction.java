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
      @Override
      public void handle(String[] args, ClientHandler client) {
         RemoteDominionPlayer p = (RemoteDominionPlayer)client.getPlayer();
         
         Card c = Card.getCard(args[0]);
         if (c == null) {
            client.write("invalidCard "+args[0]);
            return;
         }
         
         p.setRet(c);
      }

      @Override
      public boolean matches(String s) {
         return Card.getCard(s) != null;
      }
   };
   
   private static final Action chooseCardsAction = new Action("[cardName...]") {
      @Override
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
      }

      @Override
      public boolean matches(String s) {
         return Card.getCard(s) != null;
      }
   };
   
   static final Action statusAction = new Action("status") {
      public void handle(String[] args, ClientHandler client) {
         //TODO
         RemoteDominionPlayer p = (RemoteDominionPlayer) client.getPlayer();
         client.write("hand"+p.getCardColors(p.hand));
         client.write("discardSize "+p.discard.size());
         client.write("deckSize "+p.deck.size());
         client.write("board "+p.parentGame.boardMap.toString().replaceAll("[{,}]", ""));
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
         "redeem") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         RemoteDominionPlayer p = (RemoteDominionPlayer) client.getPlayer();
         List<Card> out = new ArrayList<Card>();
         for (Card c : p.hand)
            if (c.type == Card.Type.TREASURE)
               out.add(c);
         
         p.setRet(out);
      }
   }, chooseCardsAction, passAction);
   
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
   
   static final List<Action> passableCardsSelectionActions = Arrays.asList(chooseCardsAction, passAction);
   
   static final List<Action> cardsSelectionActions = Arrays.asList(chooseCardsAction, emptyAction);
}
