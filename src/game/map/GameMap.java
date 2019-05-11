package game.map;

import game.Game;
import game.Location;
import game.Unit;
import game.enums.*;
import game.Building;
import game.util.MapUtil;

import java.util.*;
import java.util.stream.Collectors;

public class GameMap {
	private Field[][] map;
	private int[][] diversityMap;
	private int width, height;
	private List<Unit> units;
	private List<Building> buildings;
	private List<Location> spawnPoints;

	private Game game;

	// Server sided use
	public GameMap(MapGenerator gm, Map<String, PlayerColor> players) {
		this.map = gm.getMap();
		this.width = map.length;
		this.height = map[0].length;

		spawnPoints = gm.getSpawnPoints();

		Random r = new Random();
		diversityMap = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				diversityMap[x][y] = r.nextInt(getFieldAt(x, y).getDiversity());
			}
		}

		units = new ArrayList<>();
		units.add(new Unit(PlayerColor.BLUE, UnitType.PANZER, 20, 20));
		units.add(new Unit(PlayerColor.BLUE, UnitType.PANZER, 15, 15));
		units.add(new Unit(PlayerColor.BLUE, UnitType.PANZER, 20, 15));
		units.add(new Unit(PlayerColor.BLUE, UnitType.PANZER, 5, 15));

		int usedSpawnpoints = 0;
		buildings = new ArrayList<>();
		for (PlayerColor p : players.values()) {
			buildings.add(new Building(spawnPoints.get(usedSpawnpoints).x, spawnPoints.get(usedSpawnpoints).y, BuildingType.BASE, p));
			usedSpawnpoints++;
		}
	}

	//Client sided use
	public GameMap(Field[][] map, List<Unit> units, List<Building> buildings, int[][] diversityMap, List<Location> spawnPoints) {
		this.map = map;
		this.width = map.length;
		this.height = (width > 0) ? map[0].length : 0;

		this.diversityMap = diversityMap;

		this.units = units;
		this.buildings = buildings;

		this.spawnPoints = spawnPoints;
	}

	public GameMap(String data) {}

	public void setGame(Game game) {
		this.game = game;
	}

	public void spawnUnit(Unit unit) {
		units.add(unit);
		updateVisibility();
	}

	public void spawnBuilding(Building building) {
		buildings.add(building);
		updateVisibility();
	}

	public Field getFieldAt(Location l) {
		if (l.x < 0 || l.x >= width || l.y < 0 || l.y >= height) return Field.VOID;

		return map[l.x][l.y];
	}

	public int getDiversityAt(Location l) {
		if (l.x < 0 || l.x >= width || l.y < 0 || l.y >= height) return 0;

		return diversityMap[l.x][l.y];
	}

	public int getDiversityAt(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) return 0;

		return diversityMap[x][y];
	}

	public Field getFieldAt(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) return Field.VOID;

		return map[x][y];
	}

	public void setFieldAt(int x, int y, Field field) {
		if (x < 0 || x >= width || y < 0 || y >= height) return;
		map[x][y] = field;
	}

	public Optional<Building> getBuildingAt(Location loc) {
		return buildings.stream().filter(b -> b.getX() == loc.x && b.getY() == loc.y).findAny();
	}

	public Optional<Building> getBuildingAt(int x, int y) {
		return buildings.stream().filter(b -> b.getX() == x && b.getY() == y).findAny();
	}

	public Optional<Unit> getUnitAt(Location loc) {
		return units.stream().filter(u -> u.getX() == loc.x && u.getY() == loc.y).findAny();
	}

	public Optional<Unit> getUnitAt(int x, int y) {
		return units.stream().filter(u -> u.getX() == x && u.getY() == y).findAny();
	}

	public void killUnit(Unit unit) {
		units = units.stream().filter(u -> !(u.getX() == unit.getX() && u.getY() == unit.getY() && u.getType() == unit.getType() && u.getPlayer() == unit.getPlayer())).collect(Collectors.toList());
		updateVisibility();
	}

	/**
	 * @param player
	 * @return all {@link Unit} of a player
	 */
	public List<Unit> playerUnits(PlayerColor player) {
		return units.stream()
				.filter(u -> u.getPlayer() == player)
				.collect(Collectors.toList());
	}

	public void moveUnit(Unit unit, int targetX, int targetY) {
		unit.moveTo(targetX, targetY);
		updateVisibility();
	}

	/**
	 * @param player
	 * @return all active {@link Unit} of a player
	 */
	public List<Unit> activePlayerUnits(PlayerColor player) {
		return units.stream()
				.filter(u -> u.getPlayer() == player)
				.filter(u -> u.getState() != UnitState.INACTIVE)
				.collect(Collectors.toList());
	}

	public List<Unit> getUnits() {
		return units;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public Unit getGameUnit(Unit unit) {
		Optional<Unit> u = getUnitAt(unit.getX(), unit.getY());
		if (u.isPresent()) return u.get();
		return null;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public List<Location> getSpawnPoints() {
		return spawnPoints;
	}

	public Location getSpawnPoint(int player) {
		return spawnPoints.get(player);
	}

	public int getMaxPlayers() {
		return spawnPoints.size();
	}

	public boolean hasVisibilityUpdate(PlayerColor playerColor) {
		return !(lastVisibility.containsKey(playerColor) && visibilityChange.containsKey(playerColor) && !visibilityChange.get(playerColor));
	}

	private Map<PlayerColor, Visibility[][]> lastVisibility = new HashMap<>();
	private Map<PlayerColor, Boolean> visibilityChange = new HashMap<>();

	public Visibility[][] getVisibilityMap(PlayerColor playerColor) {
		Visibility[][] visibilities;
		if (lastVisibility.containsKey(playerColor)) {
			visibilities = lastVisibility.get(playerColor);

			if (visibilityChange.containsKey(playerColor) && !visibilityChange.get(playerColor)) return visibilities;

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (visibilities[x][y] == Visibility.VISIBLE) visibilities[x][y] = Visibility.PARTIALLY_VISIBLE;
				}
			}
		} else {
			visibilities = new Visibility[width][height];

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					visibilities[x][y] = Visibility.HIDDEN;
				}
			}
		}

		int playerID = game.getPlayerID(playerColor) - 1;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (Unit u : units) {
					if (u.getPlayer() == playerColor) {
						if (MapUtil.getDistance(x, y, u.getX(), u.getY()) < u.getType().getViewDistance() || getSpawnPoint(playerID).distanceTo(new Location(x, y)) < 5) {
							visibilities[x][y] = Visibility.VISIBLE;
						}
					}
				}
				for (Building b : buildings) {
					if (b.getPlayer() == playerColor) {
						if (MapUtil.getDistance(x, y, b.getX(), b.getY()) < b.getType().getViewDistance()) {
							visibilities[x][y] = Visibility.VISIBLE;
						}
					}
				}
			}
		}

		visibilityChange.put(playerColor, false);
		lastVisibility.put(playerColor, visibilities);
		return visibilities;
	}

	private void updateVisibility() {
		for (PlayerColor pc : game.getPlayers().values()) {
			visibilityChange.put(pc, true);
		}
	}

	public void attack(Unit unit, Unit target) {
		unit.setState(UnitState.INACTIVE);
		if (target.attackThisUnit(unit))
			killUnit(target);
	}
}
