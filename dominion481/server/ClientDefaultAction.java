package dominion481.server;

public enum ClientDefaultAction implements Action {
   GETMODE("getMode") {
      @Override
      public void handle(String[] args, ClientHandlerThread thread) {
         thread.out.println("listMode "+thread.mode);
      }
   },
   GETACTIONS("getActions") {
      @Override
      public void handle(String[] args, ClientHandlerThread thread) {
         StringBuilder build = new StringBuilder("listActions ");
         for (Object e : thread.actions.getDeclaringClass().getEnumConstants()) {
            build.append(e);
            build.append(' ');
         }
         for (ClientDefaultAction s : ClientDefaultAction.values()) {
            build.append(s);
            build.append(' ');
         }
         build.deleteCharAt(build.length()-1);
         thread.out.println(build);
      }
   },
   GETUSERS("getUsers") {
      @Override
      public void handle(String[] args, ClientHandlerThread thread) {
         StringBuilder build = new StringBuilder("listUsers ");
         for (ClientHandlerThread t : thread.server.threads) {
            build.append(t.nick);
            build.append(' ');
         }
         build.deleteCharAt(build.length()-1);
         thread.out.println(build);
      }
   };
   
   private String[] names;
   
   private ClientDefaultAction(String... names) {
      this.names = names;
   }
   
   public String toString() {
      return names[0];
   }
   
   public String[] getNames() {
      return names;
   }
}
