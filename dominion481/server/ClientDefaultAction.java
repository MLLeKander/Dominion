package dominion481.server;

public enum ClientDefaultAction implements Action {
   GETMODE("getMode") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write("listMode " + client.mode);
      }
   },
   GETACTIONS("getActions") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write(GameServer.listToString("listActions",
               ClientDefaultAction.values(), client.mode.getEnum(client)
                     .getEnumConstants()));
      }
   },
   GETUSERS("getUsers") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write(GameServer.listToString("listUsers",
               client.server.clients));
      }
   };

   private String[] names;

   private ClientDefaultAction(String... names) {
      this.names = names;
   }

   public String toString() {
      return names[0];
   }

   public String[] getNames() {
      return names;
   }
}
