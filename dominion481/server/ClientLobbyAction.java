package dominion481.server;

public enum ClientLobbyAction implements Action {
   LEAVELOBBY("leaveLobby") {
      @Override
      public void handle(String[] args, ClientHandlerThread thread) {
         thread.actions = ClientServerAction.NICK;
         thread.mode = Mode.SERVER;
      }
   },
   STARTGAME("startGame") {
      @Override
      public void handle(String[] args, ClientHandlerThread thread) {
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
