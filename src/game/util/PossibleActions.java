package game.util;

import game.Location;
import game.enums.Direction;

import java.util.List;
import java.util.Map;

public class PossibleActions {
	private List<Location> movements;
	private Map<Location, List<Direction>> directions;

	private List<Location> attacks;

	public PossibleActions(List<Location> movements, Map<Location, List<Direction>> directions, List<Location> attacks) {
		this.movements = movements;
		this.directions = directions;
		this.attacks = attacks;
	}

	public List<Location> canMoveTo() {
		return movements;
	}

	public List<Direction> moveTo(Location location) {
		return null;
	}

	public List<Location> canAttack() {
		return attacks;
	}
}
