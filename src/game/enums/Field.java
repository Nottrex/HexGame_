package game.enums;

/**
 * The type of one Hexagon
 */
public enum Field {
	WATER("Water", 1, true, true, 1),
	FOREST("Forest", 2, false, true, 1),
	GRASS("Grass", 1, false, true, 1),
	GRASS_ROCK("Grass-rock", 3, false, true, 2),
	DIRT("Dirt", 1, false, true, 2),
	DIRT_ROCK("Dirt-rock", 3, false, true, 1),
	SAND("Sand", 2, false, true, 2),
	SNOW("Snow", 2, false, true, 1),
	STONE("Stone", 1, false, true, 1),
	VOID("Void", 999999999, false, false, 1);

	private String displayName;
	private int movementCost;
	private int diversity;
	private boolean waterTile;
	private boolean accessible;

	Field(String displayName, int movementCost, boolean waterTile, boolean accessible, int diversity) {
		this.displayName = displayName;
		this.movementCost = movementCost;
		this.diversity = diversity;
		this.waterTile = waterTile;
		this.accessible = accessible;
	}

	public boolean isAccessible() { return accessible; }

	public boolean isWaterTile() {
		return waterTile;
	}

	public int getMovementCost() {
		return movementCost;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getDiversity() { return diversity; }

}
