package dominion481.game;
abstract class Player {
   protected final DominionState state;
   protected final long upi;
   
   public abstract void takeTurn();
   public abstract void notifyActions();
   public abstract int getVictoryPoints();
   public Player(DominionState state, long upi) {
      this.state = state;
      this.upi = upi;
   }
}