package dominion481.server;

public enum ClientServerAction implements Action {
   NICK("nick") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         // TODO Nick uniqueness
         if (args.length < 2) {
            client.write("tooFewArguments");
            return;
         }
         client.rename(args[1]);
      }
   },
   GETGAMES("getGames") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write(GameServer.listToString("listGames",
               client.server.availableGames));
      }
   },
   GETLOBBIES("getLobbies") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write(GameServer.listToString("listLobbies",
               client.server.lobbies));
      }
   },
   STARTLOBBY("startLobby") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         if (args.length < 2) {
            client.write("tooFewArguments");
            return;
         }

         GameFactory factory = client.server.findFactory(args[1]);

         if (factory == null) {
            client.write("unknownGame " + args[1]);
            return;
         }

         client.joinLobby(client.server.startLobby(factory));
      }
   },
   JOINLOBBY("joinLobby") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         if (args.length < 2) {
            client.write("tooFewArguments");
            return;
         }

         Lobby l = client.server.findLobby(args[1]);

         if (l == null) {
            client.write("unknownLobby " + args[1]);
            return;
         }

         client.joinLobby(l);
      }
   };

   private String[] names;

   ClientServerAction(String... names) {
      this.names = names;
   }

   public String[] getNames() {
      return names;
   }

   public String toString() {
      return names[0];
   }
}