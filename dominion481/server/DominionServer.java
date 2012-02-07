package dominion481.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class DominionServer {
   long guestCount = 1;

   final int port;
   final List<ClientHandlerThread> threads = new ArrayList<ClientHandlerThread>();
   final List<Lobby> lobbies = new ArrayList<Lobby>();

   public DominionServer(int port) {
      this.port = port;
   }

   public void start() throws IOException {
      ServerSocket sock = new ServerSocket(port);
      while (true) {
         ClientHandlerThread cht = new ClientHandlerThread(sock.accept(), this);
         guestCount++;
         threads.add(cht);
         cht.start();
      }
   }

   public static void main(String[] args) throws IOException {
      int port = args.length > 0 ? Integer.parseInt(args[0]) : 1234;
      new DominionServer(port).start();
   }
}
