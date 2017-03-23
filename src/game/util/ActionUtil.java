package game.util;

import game.Game;
import game.GameMap;
import game.Location;
import game.Unit;
import game.enums.Direction;
import game.enums.Field;

import java.util.*;

public class ActionUtil {
	public static PossibleActions getPossibleActions(Game game, Unit unit) {
		GameMap map = game.getMap();

		Map<Location, Integer> directionLength = new HashMap<>();
		Map<Location, List<Direction>> directions = new HashMap<>();
		List<Location> attackables = new ArrayList<>();
 		PriorityQueue<Location> open = new PriorityQueue<>((o1, o2) -> (int) Math.signum(directionLength.get(o1)-directionLength.get(o2)));

		Location start = new Location(unit.getX(), unit.getY());
		directions.put(start, new ArrayList<>());
		directionLength.put(start, 0);

		open.add(start);

		while (!open.isEmpty()) {
			Location loc = open.poll();

			for (Direction d: Direction.values()) {
				Location loc2 = d.applyMovement(loc);

				int distance = directionLength.get(loc) + map.getFieldAt(loc2).getMovementCost();

				Optional<Unit> u = game.getUnitAt(loc2);

				if (map.getFieldAt(loc2) == Field.VOID || distance > unit.getType().getMovementDistance() + unit.getType().getMaxAttackDistance()) continue;
				if((u.isPresent() && u.get().getPlayer() != unit.getPlayer())) {										//TODO: Check if attackable player is in attackrange
					if(attackables.contains(loc2)) continue;
					attackables.add(loc2);
					continue;
				}
				if (distance > unit.getType().getMovementDistance()) continue;

				if (directionLength.containsKey(loc2)) {
					if (directionLength.get(loc2) <= distance) continue;

					List<Direction> dir = new ArrayList<>(directions.get(loc));
					dir.add(d);
					directions.put(loc2, dir);
					directionLength.put(loc2, distance);
				} else {
					List<Direction> dir = new ArrayList<>(directions.get(loc));
					dir.add(d);
					directions.put(loc2, dir);
					directionLength.put(loc2, distance);
					open.add(loc2);
				}
			}
		}

		List<Location> found = new ArrayList<>();
		for (Location loc: directions.keySet()) {
			Optional<Unit> u = game.getUnitAt(loc);

			if (!loc.equals(start) && (!u.isPresent())) {
				found.add(loc);
			}
		}

		return new PossibleActions(found, directions, attackables);
	}
}
