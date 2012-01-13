import java.util.*;
import static java.lang.Math.*;

public class Board {
   private final static int BOARD_SIZE = 10;

   private final static HashMap<KingdomCard, Integer> DEFAULT_BOARD =
      new HashMap<KingdomCard, Integer>();
   static {
      //DEFAULT_BOARD.put(KingdomCard.PROVIDENCE, 8);
   }

   public HashMap<KingdomCard, Integer> boardMap = new HashMap<KingdomCard, Integer>();

   public Board(List<KingdomCard> cardSet) {
      assert cardSet.size() >= BOARD_SIZE;

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

      return boardMap.get(KingdomCard.PROVIDENCE) == 0 || emptyCount >= 3;
   }
}
