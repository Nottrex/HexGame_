package game.enums;

import game.TextureHandler;

/**
 * The type of one Hexagon
 * 
 */
public enum Field {
	WATER("water", "Water", 4), FOREST("forest", "Forest", 2), GRASS("grass", "Grass", 1), GRASS_ROCK("grass_rock", "Grass-rock", 3), DIRT("dirt", "Dirt", 1), DIRT_ROCK("dirt_rock", "Dirt-rock", 3), SAND("sand", "Sand", 2), MARS("mars", "Mars", 1), STONE("stone", "Stone", 1), VOID(null, "Void", 999999999);


	private String textureName, displayName;
	private int movementCost;

	Field(String textureName, String displayName, int movementCost) {
		this.textureName = textureName;
		this.displayName = displayName;
		this.movementCost = movementCost;
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
