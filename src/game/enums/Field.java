package game.enums;

/**
 * The type of one Hexagon
 * 
 */
public enum Field {
	WATER("Water", 1, true, true),
	FOREST("Forest", 2, false, true),
	GRASS("Grass", 1, false, true),
	GRASS_ROCK("Grass-rock", 3, false, true),
	DIRT("Dirt", 1, false, true),
	DIRT_ROCK("Dirt-rock", 3, false, true),
	SAND("Sand", 2, false, true),
	MARS("Mars", 1, false, true),
	STONE("Stone", 1, false, true),
	VOID("Void", 999999999, false, false);


	private String displayName;
	private int movementCost;
	private boolean waterTile;
	private boolean accessible;

	Field(String displayName, int movementCost, boolean waterTile, boolean accessible) {
		this.displayName = displayName;
		this.movementCost = movementCost;
		this.waterTile = waterTile;
		this.accessible = accessible;
	}

	public boolean isAccessible() {
		return accessible;
	}

	public boolean isWaterTile() {
		return waterTile;
	}

	public int getMovementCost() {
		return movementCost;
	}

	public String getDisplayName() {
		return displayName;
	}

}
