package dominion481.server;

import java.io.PrintWriter;

public enum ClientServerAction {
   NICK("nick") {
      public void handle(String line, PrintWriter out) {
         
      }
   }, GETGAMES("getGames") {
      public void handle(String line, PrintWriter out) {
         ;
      }
   };
   
   String name;
   public abstract void handle(String line, PrintWriter out);
   ClientServerAction(String n) { this.name = n; }
}