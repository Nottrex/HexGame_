package game.enums;

import game.TextureHandler;

public enum UnitType {
	TANK("tank", "Tank", 0.75, 3);
	
	private int movementDistance;
	private double size;
	private String textureName, displayName;

	UnitType(String textureName, String displayName, double size, int movementDistance) {
		this.movementDistance = movementDistance;
		this.textureName = textureName;
		this.displayName = displayName;
		this.size = size;
	}

	public String getTextureName() {
		return textureName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getMovementDistance() {
		return movementDistance;
	}

	public double getSize() {
		return size;
	}

	static {
		for (UnitType ut: UnitType.values()) {
			for (PlayerColor pc: PlayerColor.values()) {
				TextureHandler.loadImagePng("units_" + ut.getTextureName() + "_" + pc.getTextureName(), "units/" + ut.getTextureName() + "/" + pc.getTextureName());
			}
		}
	}
}
