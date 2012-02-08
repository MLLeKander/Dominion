package dominion481.game;

import java.util.List;


public class Game {
   List<Class<? extends Player>> playerClasses;
   
   public Game(List<Class<? extends Player>> playerClasses) {
      this.playerClasses = playerClasses;
   }
}
