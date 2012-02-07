package dominion481.server;

public interface Action {
   public void handle(String[] args, ClientHandler thread);
   public String[] getNames();
}
