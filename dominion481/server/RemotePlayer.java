package dominion481.server;

import java.util.List;

public interface RemotePlayer {
   public ClientHandler getClient();
   public List<Action> getActions();
}
