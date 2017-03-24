package game.enums;

import game.TextureHandler;

/**
 * The type of one Hexagon
 * 
 */
public enum Field {
	WATER("water", "Water", 1, true, true),
	FOREST("forest", "Forest", 2, false, true),
	GRASS("grass", "Grass", 1, false, true),
	GRASS_ROCK("grass_rock", "Grass-rock", 3, false, true),
	DIRT("dirt", "Dirt", 1, false, true),
	DIRT_ROCK("dirt_rock", "Dirt-rock", 3, false, true),
	SAND("sand", "Sand", 2, false, true),
	MARS("mars", "Mars", 1, false, true),
	STONE("stone", "Stone", 1, false, true),
	VOID(null, "Void", 999999999, false, false);


	private String textureName, displayName;
	private int movementCost;
	private boolean waterTile;
	private boolean accessible;

	Field(String textureName, String displayName, int movementCost, boolean waterTile, boolean accessible) {
		this.textureName = textureName;
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

	public String getTextureName() {
		return textureName;
	}

	public String getDisplayName() {
		return displayName;
	}

	static {
		for (Field f: Field.values()) {
			if (f.getTextureName() != null) {
				TextureHandler.loadImagePng("field_" + f.getTextureName(), "field/" + f.getTextureName());
			}
		}
	}
}
