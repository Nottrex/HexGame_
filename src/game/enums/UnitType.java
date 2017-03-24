package game.enums;

import game.TextureHandler;

public enum UnitType {
	TANK("tank", "Tank", 0.75, 5, 1, 2, false, false);
	
	private int movementDistance;
	private int minAttackDistance, maxAttackDistance;
	private double size;
	private String textureName, displayName;
	private boolean flying;
	private boolean swimming;

	UnitType(String textureName, String displayName, double size, int movementDistance, int minAttackDistance, int maxAttackDistance, boolean flying, boolean swimming) {
		this.movementDistance = movementDistance;
		this.textureName = textureName;
		this.displayName = displayName;
		this.size = size;
		this.minAttackDistance = minAttackDistance;
		this.maxAttackDistance = maxAttackDistance;

		this.flying = flying;
		this.swimming = swimming;
	}

	public boolean isFlying() {
		return flying;
	}

	public boolean isSwimming() {
		return swimming;
	}

	public String getTextureName() {
		return textureName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getMinAttackDistance() {
		return minAttackDistance;
	}

	public int getMaxAttackDistance() {
		return maxAttackDistance;
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
