package dominion481.game;
public enum Card {
	Province(null, 8, 7, 0),
	Estate(null, 0, 0, 0),
	Duchy(null, 0, 0, 0);
	
	public enum Type {
		ACTION, VICTORY, TREASURE;
	}
	
	private final int vp, cost, treasureValue;
	public int getVp() { return vp; }
	public int getCost() { return cost; }
	public int getTreasureValue() { return treasureValue; }
	
	public final Type type;
	
	public void play() {}
	public void react() {}
	
	private Card(Type type, int cost, int vp, int treasureValue) {
		this.cost = cost;
		this.vp = vp;
		this.treasureValue = treasureValue;
		this.type = type;
	}
}