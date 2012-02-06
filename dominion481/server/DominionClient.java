package dominion481.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class DominionClient {
   public static void main(String[] args) throws IOException {
      Scanner stdin = new Scanner(System.in);
      
      Socket sock = new Socket("localhost", 1234);
      
      PrintWriter serverout = new PrintWriter(sock.getOutputStream(), true);
      final Scanner serverin = new Scanner(new InputStreamReader(sock.getInputStream()));
      
      new Thread(){
         public void run() {
            while (serverin.hasNextLine())
               // What if the server responds in the middle of typing?
               System.out.println("< "+serverin.nextLine());
            System.exit(-1);
         }
      }.start();
      
      while (stdin.hasNextLine()) {
         serverout.println(stdin.nextLine());
      }
   }
}
