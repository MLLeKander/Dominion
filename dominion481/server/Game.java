package dominion481.server;

public interface Game {
   @SuppressWarnings("rawtypes")
   public Class<? extends Enum> getActions(ClientHandler client);

}
