package dominion481.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dominion481.server.ClientHandler;
import dominion481.server.Game;
import dominion481.server.GameFactory;

public class DominionFactory extends GameFactory {
   public Game createGame(List<ClientHandler> clients) {
      List<Card> cards = new ArrayList<Card>(Arrays.asList(Card.values()));
      Dominion out = new Dominion(cards);
      
      for (ClientHandler client : clients) {
         out.addPlayer(new RemoteDominionPlayer(out, client));
         client.game = out;
      }
      
      return out;
   }
   
   public String getName() {
      return "Dominion";
   }
}
