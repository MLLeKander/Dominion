package dominion481.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class DominionClient {
   private final static int DEFAULT_PORT = 1234;

   public static void main(String[] args) throws IOException {
      final Scanner stdin = new Scanner(System.in), serverin;
      PrintWriter serverout;
      Socket sock;

      if (args.length == 0)
         sock = new Socket("localhost", DEFAULT_PORT);
      else if (args.length == 1)
         sock = new Socket(args[0], DEFAULT_PORT);
      else
         sock = new Socket(args[0], Integer.parseInt(args[1]));

      serverout = new PrintWriter(sock.getOutputStream(), true);
      serverin = new Scanner(new InputStreamReader(
            sock.getInputStream()));

      new Thread() {
         public void run() {
            while (serverin.hasNextLine())
               // TODO What if the server responds in the middle of typing?
               System.out.println("< " + serverin.nextLine());
            System.exit(0);
         }
      }.start();

      while (stdin.hasNextLine()) {
         serverout.println(stdin.nextLine());
      }
      serverout.close();
   }
}