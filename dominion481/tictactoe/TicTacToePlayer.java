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

   List<Action> turnActions = Arrays.asList(new Action("") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         
      }
      
      @Override
      public boolean match(String s) {
         
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
      
   }
}
