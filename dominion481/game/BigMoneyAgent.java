package dominion481.game;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BigMoneyAgent {
   private final static int DEFAULT_PORT = 1234;

   /**
    * @param args
    * @throws IOException
    */
   public static void main(String[] args) throws IOException {
      final Scanner serverin;
      final PrintWriter serverout;
      final Socket sock;
      String line;

      if (args.length == 0)
         sock = new Socket("localhost", DEFAULT_PORT);
      else if (args.length == 1)
         sock = new Socket(args[0], DEFAULT_PORT);
      else
         sock = new Socket(args[0], Integer.parseInt(args[1]));

      serverout = new PrintWriter(sock.getOutputStream(), true);
      serverin = new Scanner(new InputStreamReader(sock.getInputStream()));

      assert (serverin.nextLine().startsWith("serverWelcome Guest"));
      echo("nick BigMoney", serverout);

      line = readUntil("lobbyWelcome Dominion.", serverin);

      String lobbyNumber = line.split("\\s+")[1].split("\\.")[1];
      echo("join " + lobbyNumber, serverout);

      line = readUntil("glhf", serverin);

      for (int i = 0; i < 20; i++) {
         standardTurn(serverin, serverout, Card.Type.TREASURE.colorCode);
      }

      while (true) {
         standardTurn(serverin, serverout, Card.Type.VICTORY.colorCode);
      }
   }

   public static void standardTurn(Scanner in, PrintWriter out, String prefix) {
      String card = "";
      readUntil("treasurePhase", in);
      echo("redeem", out);

      for (String s : readUntil("buyPhase", in).split("\\s+")) {
         if (s.startsWith(prefix) || s.equals(Card.PROVINCE.getColorName())) {
            card = s.replaceAll("\033\\[\\d+m", "");
            break;
         }
      }
      echo("buy " + card, out);
   }

   public static String readUntil(String prefix, Scanner in) {
      String line;
      while (!(line = getLine(in)).startsWith(prefix))
         ;
      return line;
   }

   public static String getLine(Scanner in) {
      if (!in.hasNextLine())
         return null;
      String line = in.nextLine();
      System.out.println("< " + line);
      return line;
   }

   public static void echo(String line, PrintWriter out) {
      System.out.println(line);
      out.println(line);
   }
}