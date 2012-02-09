package dominion481.server;

import java.util.List;

public abstract class Game extends Thread {
   public abstract List<RemotePlayer> getRemotePlayers();
   
   public void notifyAll(String message) {
      GameServer.log("notifyAllGame", message);
      
      for (RemotePlayer p : getRemotePlayers())
         p.getClient().write(message);
   }
}
