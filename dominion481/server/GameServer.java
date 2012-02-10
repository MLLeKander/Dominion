package dominion481.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import dominion481.game.DominionFactory;

public class GameServer {
   long guestCount = 1;

   final int port;
   final List<ClientHandler> clients = new ArrayList<ClientHandler>();
   final List<Lobby> lobbies = new ArrayList<Lobby>();
   final List<Game> games = new ArrayList<Game>();
   final List<GameFactory> availableGames = new ArrayList<GameFactory>();

   public GameServer(int port) {
      availableGames.add(new DominionFactory());

      this.port = port;
   }

   public void start() throws IOException {
      ServerSocket sock = new ServerSocket(port);
      while (true) {
         ClientHandler client = new ClientHandler(sock.accept(), this);
         guestCount++;
         clients.add(client);
         client.start();
      }
   }

   public void notifyAll(String message) {
      log("notifyAllServer", message);

      for (ClientHandler t : clients)
         if (t.mode == Mode.SERVER)
            t.write(message);
   }

   public static void main(String[] args) throws IOException {
      int port = args.length > 0 ? Integer.parseInt(args[0]) : 1234;
      new GameServer(port).start();
   }

   public static void join(StringBuilder s, Object[] arr) {
      for (Object o : arr)
         s.append(' ').append(o);
   }

   public static void join(StringBuilder s, Iterable<?> obj) {
      for (Object o : obj)
         s.append(' ').append(o);
   }

   public static String listToString(String action, Object[]... arrs) {
      StringBuilder s = new StringBuilder(action);
      for (Object[] arr : arrs)
         join(s, arr);
      return s.toString();
   }

   public static String listToString(String action, Iterable<?>... objs) {
      StringBuilder s = new StringBuilder(action);
      for (Iterable<?> obj : objs)
         join(s, obj);
      return s.toString();
   }

   public Lobby startLobby(GameFactory factory) {
      Lobby l = new Lobby(factory, this);
      lobbies.add(l);
      return l;
   }

   public Lobby findLobby(String name) {
      try {
         int id = Integer.valueOf(name);
         for (Lobby l : lobbies)
            if (l.id == id)
               return l;
      } catch (NumberFormatException e) {
         for (Lobby l : lobbies)
            if (l.toString().equalsIgnoreCase(name))
               return l;
      }
      return null;
   }

   public GameFactory getFactory(String name) {
      GameFactory factory = null;
      for (GameFactory gf : availableGames)
         if (gf.getName().equalsIgnoreCase(name)) {
            factory = gf;
            break;
         }
      return factory;
   }
   
   public static void log(String to, String message) {
      System.out.println(to + ": " + message);
   }
}
