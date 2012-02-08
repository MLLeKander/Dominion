package dominion481.game;
import java.util.ArrayList;
import java.util.List;

public class Dominion {
   List<Player> players;
   List<Card> cardSet;
   GameState board;
   
   public Dominion(List<Class<? extends Player>> players, List<Card> cardSet) {
      //this.players = players;
      this.cardSet = cardSet;
   }

   public List<Player> play() {
      board = new GameState(players, cardSet);

      while (true) {
         for (Player p : players) {
            p.takeTurn();
            if (board.isGameOver()) {
               return getWinners();
            }
         }
      }

      //return null;
   }

   public List<Player> getWinners() {
      int max = -1;
      List<Player> maxPs = null;

      for (Player p : players) {
         int points = p.getVictoryPoints();
         if (points > max) {
            maxPs = new ArrayList<Player>();
            max = points;
         }
         
         if (points >= max)
            maxPs.add(p);
      }
      
      return maxPs;
   }

   public static void main(String[] args) {

   }

   public List<Player> getPlyers() {
      return players;
   }

   public GameState getBoard() {
      return board;
   }
}


