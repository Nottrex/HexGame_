package game.util;

import game.Location;
import game.enums.Direction;

import java.util.List;
import java.util.Map;

public class PossibleActions {
	private List<Location> movements;
	private Map<Location, List<Direction>> directions;

	private List<Location> attacks;
	private Map<Location, List<Direction>> attack_directions;

	public PossibleActions(List<Location> movements, Map<Location, List<Direction>> directions, List<Location> attacks, Map<Location, List<Direction>> attack_directions) {
		this.movements = movements;
		this.directions = directions;
		this.attacks = attacks;
		this.attack_directions = attack_directions;
	}

	public List<Location> canMoveTo() {
		return movements;
	}

	public List<Direction> moveTo(Location location) {
		return directions.get(location);
	}

	public List<Location> canAttack() {
		return attacks;
	}

	public List<Direction> moveToToAttack(Location location) {
		return attack_directions.get(location);
	}
}
