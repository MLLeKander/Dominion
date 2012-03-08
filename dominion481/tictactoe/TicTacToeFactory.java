package dominion481.tictactoe;

import java.util.List;

import dominion481.server.ClientHandler;
import dominion481.server.Game;
import dominion481.server.GameFactory;

public class TicTacToeFactory extends GameFactory {

   @Override
   public Game createGame(List<ClientHandler> clients) {
      // TODO Auto-generated method stub
      TicTacToe out = new TicTacToe();

      for (ClientHandler client : clients) {
         out.addPlayer(new TicTacToePlayer(out, client));
         client.game = out;
      }
      
      return out;
   }

   @Override
   public String getName() {
      return "TicTacToe";
   }

}
