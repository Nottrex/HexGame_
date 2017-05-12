package game.enums;

import client.i18n.Strings;

/**
 * The type of one Hexagon
 */
public enum Field {
	WATER(Strings.get("Water"), 1, true, true, 1),
	FOREST(Strings.get("Forest"), 2, false, true, 1),
	GRASS(Strings.get("Grass"), 1, false, true, 1),
	GRASS_ROCK(Strings.get("Grass_Rock"), 3, false, true, 2),
	DIRT(Strings.get("Dirt"), 1, false, true, 2),
	DIRT_ROCK(Strings.get("Dirt_Rock"), 3, false, true, 1),
	SAND(Strings.get("Sand"), 2, false, true, 2),
	SNOW(Strings.get("Snow"), 2, false, true, 1),
	STONE(Strings.get("Stone"), 1, false, true, 1),
	VOID(Strings.get("Void"), 999999999, false, false, 1);

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
