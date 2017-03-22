package game.enums;

import game.TextureHandler;

/**
 * The type of one Hexagon
 * 
 */
public enum Field {
	FOREST("forest", "Forest", true, true, true), GRASS("grass", "Grass", true, true, false), GRASS_ROCK("grass_rock", "Grass-rock", false, true, true), DIRT("dirt", "Dirt", true, true, true), DIRT_ROCK("dirt_rock", "Dirt-rock", false, true, true), SAND("sand", "Sand", true, true, true), MARS("mars", "Mars", true, true, false), STONE("stone", "Stone", true, true, false), VOID(null, "Void", false, false, true);

	
	private boolean accessNormal, accessFlight, movementReduction;
	private String textureName, displayName;

	Field(String textureName, String displayName, boolean accessNormal, boolean accessFlight, boolean movementReduction) {
		this.textureName = textureName;
		this.accessNormal = accessNormal;
		this.accessFlight = accessFlight;
		this.movementReduction = movementReduction;
		this.displayName = displayName;
	}
	
	public boolean isAccessibleNormal() {
		return accessNormal;
	}
	
	public boolean isAccessibleFlight() {
		return accessFlight;
	}
	
	public boolean isMovementReduced() {
		return movementReduction;
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
