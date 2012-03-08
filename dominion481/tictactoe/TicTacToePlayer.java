package dominion481.tictactoe;

import java.util.Arrays;
import java.util.List;

import dominion481.server.Action;
import dominion481.server.ClientHandler;
import dominion481.server.RemotePlayer;

public class TicTacToePlayer implements RemotePlayer {
   boolean myTurn = false;
   ClientHandler client;
   TicTacToe game;
   int x, y;

   List<Action> turnActions = Arrays.asList(new Action("[x y]") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         try {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
            synchronized (client) { client.notify(); }
         } catch (NumberFormatException e) {
            client.write("invalidLocation ("+args[0]+","+args[1]+")");
         }
      }
      
      @Override
      public boolean matches(String s) {
         if (super.matches(s))
            return true;
         try {
            Integer.valueOf(s);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      }
   }, Action.emptyAction);
   List<Action> notTurnActions = Arrays.asList();

   public TicTacToePlayer(TicTacToe game, ClientHandler client) {
      this.client = client;
      this.game = game;
   }

   @Override
   public ClientHandler getClient() {
      return client;
   }

   @Override
   public List<Action> getActions() {
      return myTurn ? turnActions : notTurnActions;
   }
   
   @Override
   public String toString() {
      return client.getNick();
   }

   public void takeTurn() {
      myTurn = true;
      while (myTurn) {
         synchronized (client) { 
            try {
               client.wait();
            } catch (InterruptedException e) {
               throw new RuntimeException(e);
            }
         }
         try {
            game.play(this, x, y);
            myTurn = false;
         } catch(IllegalArgumentException e) {
            client.write("invalidLocation ("+x+","+y+")");
         }
      }
      myTurn = false;
   }
}
