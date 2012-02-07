package dominion481.server;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
   int id;
   private GameFactory game;
   private List<ClientHandler> occupants = new ArrayList<ClientHandler>();
   private GameServer server;

   public Lobby(GameFactory game, GameServer server) {
      this.game = game;
      this.server = server;
      id = game.getLobbyId();
   }

   public String toString() {
      return game.getName() + "." + id;
   }

   public void add(ClientHandler client) {
      occupants.add(client);
   }

   public void remove(ClientHandler client) {
      occupants.remove(client);
      if (occupants.size() == 0)
         server.lobbies.remove(this);
   }

   public void notifyAll(String message) {
      System.out.println("notifyAllLobby " + toString() + ": " + message);

      for (ClientHandler client : occupants)
         client.write(message);
   }
}
