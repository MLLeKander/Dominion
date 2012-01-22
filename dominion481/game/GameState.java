package dominion481.game;
import static java.lang.Math.floor;
import static java.lang.Math.random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
   private final static int BOARD_SIZE = 10;

   private final static HashMap<Card, Integer> DEFAULT_BOARD =
      new HashMap<Card, Integer>();
   static {
      DEFAULT_BOARD.put(Card.Province, 8);
      DEFAULT_BOARD.put(Card.Estate, 8);
      DEFAULT_BOARD.put(Card.Duchy, 8);
   }
   
   public HashMap<Card, Integer> boardMap = new HashMap<Card, Integer>();
   
   List<Player> players;
   
   Player currentTurn;
   int actions;
   int buys;
   int coin;
   
   Player currentTarget;
   List<Card> inPlay;
   Map<Player, List<Card>> hands;
   Map<Player, List<Card>> decks;
   Map<Player, List<Card>> discards;
   
   
   public GameState(List<Player> players, List<Card> cardSet) {
      assert cardSet.size() >= BOARD_SIZE;
      
      this.players = players;

      for (int i = 0, choice = 0; i < BOARD_SIZE; i++, choice++) {
         int max = cardSet.size() - BOARD_SIZE + i + 1;
         choice += floor(random() * (max - choice));
         boardMap.put(cardSet.get(choice), 10);
      }
	      
      boardMap.putAll(DEFAULT_BOARD);
   }

   public boolean isGameOver() {
      int emptyCount = 0;
      for (int i : boardMap.values())
         if (i == 0)
            emptyCount++;

      return boardMap.get(Card.Province) == 0 || emptyCount >= 3;
   }
}
