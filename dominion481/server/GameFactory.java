package dominion481.server;

import java.util.List;

public abstract class GameFactory {
   private static int gameCount = 1;

   public abstract Game createGame(List<ClientHandler> clients);

   public abstract String getName();

   public final int getLobbyId() {
      return gameCount++;
   }

   public final String toString() {
      return getName();
   }
}
