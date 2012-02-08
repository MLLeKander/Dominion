package dominion481.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandlerThread extends Thread {
   final Socket sock;
   final DominionServer server;
   String nick;
   
   @SuppressWarnings("rawtypes")
   Class actionsClass = ClientServerAction.class;

   final PrintWriter out;
   final Scanner in;

   public ClientHandlerThread(Socket sock, DominionServer server)
         throws IOException {
      super();
      this.sock = sock;
      this.server = server;
      this.in = new Scanner(new InputStreamReader(sock.getInputStream()));
      this.out = new PrintWriter(sock.getOutputStream(), true);
      nick = "Guest" + server.guestCount;
   }

   public void run() {
      out.println("serverWelcome " + nick);

      while (in.hasNextLine()) {
         String line = in.nextLine();
         System.out.println(nick + ": " + line);
         respondTo(line.trim().split("\\s+"));
      }
   }

   @SuppressWarnings("unchecked")
   private void respondTo(String[] args) {
      if (args.length == 0)
         return;

      try {
         ((Action) Enum.valueOf(actionsClass, args[0].toUpperCase())).handle(
               args, this);
      } catch (IllegalArgumentException e) {
         out.println("unknownAction " + args[0]);
      }
   }
}