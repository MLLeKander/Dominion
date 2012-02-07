package dominion481.server;

public abstract class Game {
   public abstract String getName();
   
   public static Game startGame(Class<? extends Game> gClass, int players) {
      return null;
   }
   
}
