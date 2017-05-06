package game.enums;

import i18n.Strings;

/**
 * The type of one Hexagon
 */
public enum Field {
	WATER(Strings.FIELD_NAME_WATER, 1, true, true, 1),
	FOREST(Strings.FIELD_NAME_FOREST, 2, false, true, 1),
	GRASS(Strings.FIELD_NAME_GRASS, 1, false, true, 1),
	GRASS_ROCK(Strings.FIELD_NAME_GRASS_ROCK, 3, false, true, 2),
	DIRT(Strings.FIELD_NAME_DIRT, 1, false, true, 2),
	DIRT_ROCK(Strings.FIELD_NAME_DIRT_ROCK, 3, false, true, 1),
	SAND(Strings.FIELD_NAME_SAND, 2, false, true, 2),
	SNOW(Strings.FIELD_NAME_SNOW, 2, false, true, 1),
	STONE(Strings.FIELD_NAME_STONE, 1, false, true, 1),
	VOID(Strings.FIELD_NAME_VOID, 999999999, false, false, 1);

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
