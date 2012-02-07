package dominion481.server;

public enum ClientLobbyAction implements Action {
   LEAVELOBBY("leaveLobby") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         client.leaveLobby();
      }
   },
   STARTGAME("startGame") {
      @Override
      public void handle(String[] args, ClientHandler client) {
         ;
      }
   };
   
   private String[] names;
   
   private ClientLobbyAction(String... names) {
      this.names = names;
   }
   
   public String toString() {
      return names[0];
   }
   
   public String[] getNames() {
      return names;
   }
}
