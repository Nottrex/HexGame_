package game.util;

import game.Game;
import game.GameMap;
import game.Location;
import game.Unit;
import game.enums.Direction;
import game.enums.Field;

import java.util.*;
import java.util.stream.Collectors;

public class ActionUtil {
	public static PossibleActions getPossibleActions(Game game, Unit unit) {
		GameMap map = game.getMap();

		Map<Location, Integer> directionLength = new HashMap<>();
		Map<Location, List<Direction>> directions = new HashMap<>();
		List<Location> attackables = new ArrayList<>();
		Map<Location, List<Direction>> attackDirections = new HashMap<>();

 		PriorityQueue<Location> open = new PriorityQueue<>((o1, o2) -> (int) Math.signum(directionLength.get(o1)-directionLength.get(o2)));

		Location start = new Location(unit.getX(), unit.getY());
		directions.put(start, new ArrayList<>());
		directionLength.put(start, 0);

		open.add(start);

		while (!open.isEmpty()) {
			Location loc = open.poll();

			for (Unit u: map.getUnits()) {
				int d = MapUtil.getDistance(loc.x, loc.y, u.getX(), u.getY());
				Location a = new Location(u.getX(), u.getY());
				if (u.getPlayer() != unit.getPlayer() && d >= unit.getType().getMinAttackDistance() && d <= unit.getType().getMaxAttackDistance() && !attackables.contains(a)) {
					attackables.add(a);
					attackDirections.put(a, directions.get(loc));
				}
			}


			for (Direction d: Direction.values()) {
				Location loc2 = d.applyMovement(loc);

				boolean zoneOfControlOfEnemy = map.getUnits().stream()
						.filter(u -> u.getPlayer() != unit.getPlayer())
						.anyMatch(u -> MapUtil.getDistance(loc2.x, loc2.y, u.getX(), u.getY()) == 1);

				int distance = directionLength.get(loc) + map.getFieldAt(loc2).getMovementCost() + (zoneOfControlOfEnemy ? 1 : 0);

				Optional<Unit> u = map.getUnitAt(loc2);

				if (map.getFieldAt(loc2) == Field.VOID || distance > unit.getType().getMovementDistance() + unit.getType().getMaxAttackDistance() || (u.isPresent() && u.get().getPlayer() != unit.getPlayer())) continue;

				if (distance > unit.getType().getMovementDistance()) continue;

				if (directionLength.containsKey(loc2)) {
					if (directionLength.get(loc2) <= distance) continue;

					/*List<Direction> dir = new ArrayList<>(directions.get(loc));
					dir.add(d);
					directions.put(loc2, dir);
					directionLength.put(loc2, distance);*/
					//Can never ever happen xD

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
			Optional<Unit> u = map.getUnitAt(loc);

			if (!loc.equals(start) && (!u.isPresent())) {
				found.add(loc);
			}
		}

		return new PossibleActions(found, directions, attackables, attackDirections);
	}
}
