package dominion481.server;

public enum ClientLobbyAction implements Action {
   LEAVELOBBY("leaveLobby") {
      @Override
      public void handle(String[] args, ClientHandlerThread thread) {
         ;
      }
   },
   STARTGAME("startGame") {
      @Override
      public void handle(String[] args, ClientHandlerThread thread) {
         ;
      }
   };
   
   String name;
   
   private ClientLobbyAction(String name) {
      this.name = name;
   }
   
   public String getName() {
      return name;
   }
}
