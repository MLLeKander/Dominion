package dominion481.game;

import java.util.List;

import dominion481.server.ClientHandler;
import dominion481.server.Game;
import dominion481.server.GameFactory;

public class DominionFactory extends GameFactory {
   public Game createGame(List<ClientHandler> threads) {
      return null;
   }
   
   public String getName() {
      return "Dominion";
   }
}
