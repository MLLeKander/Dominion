package dominion481.server;

import java.util.ArrayList;
import java.util.List;

public enum Mode {
   SERVER {
      @Override
      public List<Action> getModeActions(ClientHandler client) {
         return Action.serverActions;
      }
   },
   LOBBY {
      @Override
      public List<Action> getModeActions(ClientHandler client) {
         return Action.lobbyActions;
      }
   },
   GAME {
      @Override
      public List<Action> getModeActions(ClientHandler client) {
         return client.player.getActions();
      }
   };

   public abstract List<Action> getModeActions(ClientHandler client);
   
   public List<Action> getActions(ClientHandler client) {
      List<Action> tmp = new ArrayList<Action>(Action.defaultActions);
      tmp.addAll(getModeActions(client));
      
      return tmp;
   }
}
