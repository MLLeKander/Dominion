package dominion481.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {
   final GameServer server;
   String nick;
   Mode mode = Mode.SERVER;
   Lobby lobby;
   Game game;

   private final Socket sock;
   private final PrintWriter out;
   private final Scanner in;

   public ClientHandler(Socket sock, GameServer server) throws IOException {
      super();
      this.sock = sock;
      this.server = server;
      this.in = new Scanner(new InputStreamReader(sock.getInputStream()));
      this.out = new PrintWriter(sock.getOutputStream(), true);
      nick = "Guest" + server.guestCount;
   }

   public void run() {
      server.notifyAll("serverWelcome " + nick);

      while (in.hasNextLine()) {
         String line = in.nextLine();
         System.out.println(nick + ": " + line);
         respondTo(line.trim().split("\\s+"));
      }

      // TODO This client never sees its own serverbye.
      server.notifyAll("serverBye " + nick);

      try {
         sock.close();
      } catch (IOException e) {
         System.out.println("Could not close " + nick + "'s socket.");
         System.out.println(e);
      }

      server.clients.remove(this);
   }

   public String toString() {
      return nick;
   }

   @SuppressWarnings("unchecked")
   private void respondTo(String[] args) {
      if (args.length == 0)
         return;

      try {
         ((Action) Enum.valueOf(mode.getEnum(this), args[0].toUpperCase()))
               .handle(args, this);
      } catch (IllegalArgumentException e) {
         try {
            ClientDefaultAction.valueOf(args[0].toUpperCase()).handle(args,
                  this);
         } catch (IllegalArgumentException ex) {
            out.println("unknownAction " + args[0]);
         }
      }
   }

   public void write(String s) {
      out.println(s);
   }

   public void joinLobby(Lobby l) {
      lobby = l;
      lobby.add(this);
      mode = Mode.LOBBY;

      server.notifyAll("lobbyWelcome " + lobby + " " + nick);
      lobby.notifyAll("lobbyWelcome " + lobby + " " + nick);
   }

   public void leaveLobby() {
      lobby.notifyAll("lobbyBye " + lobby + " " + nick);
      server.notifyAll("lobbyBye " + lobby + " " + nick);

      mode = Mode.SERVER;
      lobby.remove(this);
      lobby = null;
   }

   public void rename(String newNick) {
      server.notifyAll("serverRename " + nick + " " + newNick);
      this.nick = newNick;
   }
}