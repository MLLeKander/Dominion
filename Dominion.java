import java.util.*;
import static java.lang.Math.*;

public class Dominion {
   List<Player> players;
   List<KingdomCard> cardSet;
   Board board;
   
   public Dominion(List<Player> players, List<KingdomCard> cardSet) {
      this.players = players;
      this.cardSet = cardSet;
   }

   public List<Player> play() {
      board = new Board(cardSet);

      for (Player p : players)
         p.setBoard(board);

      while (true)
         for (Player p : players) {
            p.takeTurn();
            if (board.isGameOver())
               return getWinners();
         }

      return null;
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

   public Board getBoard() {
      return board;
   }
}

class KingdomCard {
	public static KingdomCard PROVIDENCE;
}
