package dominion481.server;

public interface Action {
   public void handle(String[] args, ClientHandlerThread thread);
}
