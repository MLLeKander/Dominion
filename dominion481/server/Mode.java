package dominion481.server;

@SuppressWarnings("rawtypes")
public enum Mode {
   SERVER {
      @Override
      public Class<ClientServerAction> getEnum(ClientHandler client) {
         return ClientServerAction.class;
      }
   },
   LOBBY {
      @Override
      public Class<ClientLobbyAction> getEnum(ClientHandler client) {
         return ClientLobbyAction.class;
      }
   },
   GAME {
      @Override
      public Class<? extends Enum> getEnum(ClientHandler client) {
         return null;
      }
   };

   public abstract Class<? extends Enum> getEnum(ClientHandler client);
}
