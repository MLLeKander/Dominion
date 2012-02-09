package dominion481.game;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class DominionMain {
   List<Class<? extends DominionPlayer>> playerClasses;
   List<DominionPlayer> players;
   List<Card> cardSet;
   Dominion state;

   public DominionMain(List<Class<? extends DominionPlayer>> players, List<Card> cardSet) {
      this.playerClasses = players;
      //this.players = players;
      this.cardSet = cardSet;
   }

   public List<DominionPlayer> play() throws InvocationTargetException,
         IllegalAccessException, InstantiationException, NoSuchMethodException {
      players = new ArrayList<DominionPlayer>();
      state = new Dominion(cardSet);
      
      for (Class<? extends DominionPlayer> cls : playerClasses) {
         players.add(cls.getConstructor(state.getClass(), Long.class).newInstance(state, 0));
      }

      while (true) {
         for (DominionPlayer p : players) {
            p.actionPhase();
            if (state.isGameOver()) {
               return getWinners();
            }
         }
      }

      // return null;
   }

   public List<DominionPlayer> getWinners() {
      int max = -1;
      List<DominionPlayer> maxPs = null;

      for (DominionPlayer p : players) {
         int points = p.getVictoryPoints();
         if (points > max) {
            maxPs = new ArrayList<DominionPlayer>();
            max = points;
         }

         if (points >= max) {
            maxPs.add(p);
         }
      }

      return maxPs;
   }

   public static void main(String[] args) {

   }
}
