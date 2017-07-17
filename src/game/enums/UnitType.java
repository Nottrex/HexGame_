package game.enums;

/**
 * Different types an {@link game.Unit} can have
 * Determines its stats
 */
public enum UnitType {
	PANZER("Tank", 0.75, 5, 2, 3, false, false, true, 1, 1, 2, 5, 6, 200),
	ARTILLERIE("Artillery", 0.75, 2, 6, 8, false, false, true, 1, 1, 2, 1, 10, 150),
	//FLUGABWEHR(),
	INFANTERIE("Infantry", 0.75, 3, 1, 1, false, true, true, 1, 1, 2, 1, 3, 50),
	KAVALLERIE("Cavalry", 0.75, 4, 1, 2, false, false, false, 1, 1, 2, 1, 4, 150);
	//PANZER_ARTILLERIE("Tank_Artillery", 0.75, 4, 4, 6, false, false, true);

	private int movementDistance;
	private int minAttackDistance, maxAttackDistance;
	private double size;
	private String displayName;
	private boolean flying;
	private boolean swimming;
	private boolean walking;

	private int maxStackSize;
	private int health, att, def;
	private int cost;

	private int viewDistance;

	UnitType(String displayName, double size, int movementDistance, int minAttackDistance, int maxAttackDistance, boolean flying, boolean swimming, boolean walking, int stackSize, int hp, int att, int def, int viewDistance, int cost) {
		this.movementDistance = movementDistance;
		this.displayName = displayName;
		this.size = size;
		this.minAttackDistance = minAttackDistance;
		this.maxAttackDistance = maxAttackDistance;

		this.flying = flying;
		this.swimming = swimming;
		this.walking = walking;

		this.maxStackSize = stackSize;
		this.health = hp;
		this.att = att;
		this.def = def;

		this.cost = cost;
		this.viewDistance = viewDistance;
	}

	public int getViewDistance() {
		return viewDistance;
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

	public int getHealth() {
		return health;
	}

	public int getDefence() {
		return def;
	}

	public int getAttack() {
		return att;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public int getCost() {
		return cost;
	}
}
