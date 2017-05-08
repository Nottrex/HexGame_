package game.enums;

import i18n.Strings;

/**
 * Different types an {@link game.Unit} can have
 * Determines its stats
 */
public enum UnitType {
	PANZER(Strings.get("Tank"), 0.75, 5, 2, 3, false, false, true),
	ARTILLERIE(Strings.get("Artillery"), 0.75, 2, 6, 8, false, false, true),
	//FLUGABWEHR(),
	INFANTERIE(Strings.get("Infantry"), 0.75, 3, 1, 1, false, true, true),
	KAVALERIE(Strings.get("Cavalry"), 0.75, 4, 1, 2, false, false, false);
	//PANZER_ARTILLERIE("Tank_Artillery", 0.75, 4, 4, 6, false, false, true);

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
