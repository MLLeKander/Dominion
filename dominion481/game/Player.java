package dominion481.game;
abstract class Player {
   public abstract void takeTurn();
   public abstract void notifyActions();
   public abstract int getVictoryPoints();
}