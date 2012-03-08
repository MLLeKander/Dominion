package dominion481.game;

import static java.lang.Math.floor;
import static java.lang.Math.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dominion481.server.Game;
import dominion481.server.GameServer;
import dominion481.server.RemotePlayer;

public class Dominion extends Game {
   private final static int BOARD_SIZE = 10;

   public final static HashMap<Card, Integer> DEFAULT_BOARD = new HashMap<Card, Integer>();
   static {
      DEFAULT_BOARD.put(Card.Province, 8);
      DEFAULT_BOARD.put(Card.Duchy, 8);
      DEFAULT_BOARD.put(Card.Estate, 45);
      // TODO How many Estates?
      DEFAULT_BOARD.put(Card.Gold, Integer.MAX_VALUE);
      DEFAULT_BOARD.put(Card.Silver, Integer.MAX_VALUE);
      DEFAULT_BOARD.put(Card.Copper, Integer.MAX_VALUE);
   }

   HashMap<Card, Integer> boardMap = new HashMap<Card, Integer>();
   public Map<Card, Integer> unmodifiableBoardMap = Collections
         .unmodifiableMap(boardMap);

   final List<DominionPlayer> players = new ArrayList<DominionPlayer>();

   DominionPlayer currentTurn;
   DominionPlayer currentTarget;
   Phase currentPhase;

   public enum Phase {
      ACTION, TREASURE, BUY
   }

   public Dominion(List<Card> cards) {
      assert cards.size() >= BOARD_SIZE;

      for (int i = 0; i < BOARD_SIZE; i++) {
         int pivot = (int) floor(random() * (cards.size() - 1));
         Card tmp = cards.set(pivot, cards.get(i));
         cards.set(i, tmp);
         boardMap.put(tmp, 10);
      }

      boardMap.putAll(DEFAULT_BOARD);
   }

   @Override
   public void run() {
      List<DominionPlayer> winners = play();
      notifyAll(GameServer.listToString("congratulations", winners));
   }

   public List<DominionPlayer> play() {
      for (DominionPlayer player : players) {
         for (int i = 0; i < 7; i++)
            player.gain(Card.Copper);
         for (int i = 0; i < 3; i++)
            player.gain(Card.Estate);
         player.prepareTurn();
      }

      while (true) {
         for (DominionPlayer p : players) {
            if (p.nick.equals("DEBUG"))
               continue;
            currentTurn = p;
            currentPhase = Phase.ACTION;
            p.actionPhase();

            currentPhase = Phase.TREASURE;
            p.treasurePhase();

            currentPhase = Phase.BUY;
            p.buyPhase();

            p.endTurn();
            if (isGameOver()) {
               notifyAll("gameOver");
               return getWinners();
            }
         }
      }
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

   public void addPlayer(DominionPlayer player) {
      players.add(player);
   }

   public boolean isGameOver() {
      int emptyCount = 0;
      for (int i : boardMap.values())
         if (i == 0)
            emptyCount++;

      return boardMap.get(Card.Province) == 0 || emptyCount >= 3;
   }

   public Map<Card, Integer> getBoardMap() {
      return unmodifiableBoardMap;
   }

   @Override
   public List<RemotePlayer> getRemotePlayers() {
      List<RemotePlayer> out = new ArrayList<RemotePlayer>(players.size());
      for (DominionPlayer p : players)
         if (p instanceof RemotePlayer)
            out.add((RemotePlayer) p);
      return out;
   }
   
   public void reveal(DominionPlayer p, Card c) {
      notifyAll("cardReveal "+p+" "+c);
   }
}
