package game.enums;

import client.i18n.LanguageHandler;

/**
 * The type of one Hexagon
 */
public enum Field {
	WATER(LanguageHandler.get("Water"), 1, true, true, 1),
	FOREST(LanguageHandler.get("Forest"), 2, false, true, 1),
	GRASS(LanguageHandler.get("Grass"), 1, false, true, 1),
	GRASS_ROCK(LanguageHandler.get("Grass_Rock"), 3, false, true, 2),
	DIRT(LanguageHandler.get("Dirt"), 1, false, true, 2),
	DIRT_ROCK(LanguageHandler.get("Dirt_Rock"), 3, false, true, 1),
	SAND(LanguageHandler.get("Sand"), 2, false, true, 2),
	SNOW(LanguageHandler.get("Snow"), 2, false, true, 1),
	STONE(LanguageHandler.get("Stone"), 1, false, true, 1),
	VOID(LanguageHandler.get("Void"), 999999999, false, false, 1);

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
