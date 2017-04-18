package game.enums;

/**
 * Different types an {@link game.Unit} can have
 * Determines its stats
 */
public enum UnitType {
	PANZER("Tank", 0.75, 5, 1, 2, false, false, true);
	
	private int movementDistance;
	private int minAttackDistance, maxAttackDistance;
	private double size;
	private String  displayName;
	private boolean flying;
	private boolean swimming;
	private boolean walking;

	UnitType(String displayName, double size, int movementDistance, int minAttackDistance, int maxAttackDistance, boolean flying, boolean swimming, boolean walking) {
		this.movementDistance = movementDistance;
		this.displayName = displayName;
		this.size = size;
		this.minAttackDistance = minAttackDistance;
		this.maxAttackDistance = maxAttackDistance;

		this.flying = flying;
		this.swimming = swimming;
		this.walking = walking;
	}

	public boolean isWalking() {
		return walking;
	}

	public boolean isFlying() {
		return flying;
	}

	public boolean isSwimming() {
		return swimming;
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
}
