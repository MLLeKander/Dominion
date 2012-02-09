package dominion481.server;

import java.util.Arrays;
import java.util.List;

public abstract class Action {
   String[] names;

   public Action(String... names) {
      this.names = names;
   }

   public abstract void handle(String[] args, ClientHandler client);

   public String[] getNames() {
      return names;
   }

   public String toString() {
      return names[0];
   }

   final static List<Action> serverActions = Arrays.asList(new Action("nick",
         "alias") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         // TODO Nick uniqueness
         if (args.length < 2) {
            client.write("tooFewArguments");
            return;
         }
         client.rename(args[1]);
      }
   }, new Action("getGames", "games") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write("listGames", client.server.availableGames);
      }
   }, new Action("getLobbies", "lobbies") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write("listLobbies", client.server.lobbies);
      }
   }, new Action("startLobby", "start") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         if (args.length < 2) {
            client.write("tooFewArguments");
            return;
         }

         GameFactory factory = client.server.getFactory(args[1]);

         if (factory == null) {
            client.write("unknownGame " + args[1]);
            return;
         }

         client.joinLobby(client.server.startLobby(factory));
      }
   }, new Action("joinLobby", "join") {
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
   });

   final static List<Action> lobbyActions = Arrays.asList(new Action(
         "leaveLobby", "leave") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.leaveLobby();
      }
   }, new Action("startGame", "start") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.lobby.startGame();
      }
   });

   final static List<Action> defaultActions = Arrays.asList(new Action(
         "getMode", "mode") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write("listMode " + client.mode);
      }
   }, new Action("getActions", "actions") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write("listActions", client.mode.getActions(client));
      }
   }, new Action("getUsers", "users") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.write("listUsers", client.server.clients);
      }
   });
}
