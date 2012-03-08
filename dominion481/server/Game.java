package dominion481.server;

import java.util.List;

public abstract class Game extends Thread {
   public abstract List<RemotePlayer> getRemotePlayers();
   
   public final void run() {
      play();
      
      for (RemotePlayer p : getRemotePlayers())
         p.getClient().leaveLobby();
   }
   
   protected abstract void play();
   
   public void notifyAll(String message) {
      GameServer.log("notifyAllGame", message);
      
      for (RemotePlayer p : getRemotePlayers())
         p.getClient().write(message);
   }
   
   public void notifyAll(String message, Iterable<?> objs) {
      GameServer.log("notifyAllGame", message);
      
      for (RemotePlayer p : getRemotePlayers())
         p.getClient().write(message, objs);
   }
   
   public void notifyAll(String message, Object[] obs) {
      GameServer.log("notifyAllGame", message);
      
      for (RemotePlayer p : getRemotePlayers())
         p.getClient().write(message, obs);
   }
}
