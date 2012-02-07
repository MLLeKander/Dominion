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
   Mode mode = Mode.SERVER;

   @SuppressWarnings("unchecked")
   Enum actions = ClientServerAction.NICK;

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
      server.notifyAll(Mode.SERVER, "serverWelcome " + nick);

      while (in.hasNextLine()) {
         String line = in.nextLine();
         System.out.println(nick + ": " + line);
         respondTo(line.trim().split("\\s+"));
      }

      // TODO This client never sees its own serverbye.
      server.notifyAll(Mode.SERVER, "serverBye " + nick);

      try {
         sock.close();
      } catch (IOException e) {
         System.out.println("Could not close " + nick + "'s socket.");
         System.out.println(e);
      }

      server.threads.remove(this);
   }

   @SuppressWarnings("unchecked")
   private void respondTo(String[] args) {
      if (args.length == 0)
         return;

      try {
         ((Action) Enum.valueOf(actions.getDeclaringClass(), args[0]
               .toUpperCase())).handle(args, this);
      } catch (IllegalArgumentException e) {
         try {
            ClientDefaultAction.valueOf(args[0].toUpperCase()).handle(args,
                  this);
         } catch (IllegalArgumentException ex) {
            out.println("unknownAction " + args[0]);
         }
      }
   }
}