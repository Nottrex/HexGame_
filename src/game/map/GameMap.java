package game.map;

import game.Location;
import game.Unit;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitState;
import game.enums.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameMap {
	private Field[][] map;
	private int width, height;
	private List<Unit> units;

	public GameMap(MapGenerator gm) {
		this.map = gm.getMap();
		this.width = map.length;
		this.height = map[0].length;

		units = new ArrayList<>();
		units.add(new Unit(PlayerColor.BLUE, UnitType.TANK, 20, 20));
		units.add(new Unit(PlayerColor.BLUE, UnitType.TANK, 15, 15));
		units.add(new Unit(PlayerColor.RED, UnitType.TANK, 20, 15));
		units.add(new Unit(PlayerColor.RED, UnitType.TANK, 5, 15));
	}

	public GameMap(Field[][] map, List<Unit> units) {
		this.map = map;
		this.width = map.length;
		this.height = (width > 0) ? map[0].length : 0;

		this.units = units;
	}

	public Field getFieldAt(Location l) {
		if (l.x < 0 || l.x >= width || l.y < 0 || l.y >= height) return Field.VOID;

		return map[l.x][l.y];
	}

	public Field getFieldAt(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) return Field.VOID;
		
		return map[x][y];
	}
	
	public void setFieldAt(int x, int y, Field field) {
		if (x < 0 || x >= width || y < 0 || y >= height) return;
		map[x][y] = field;
	}

	public Optional<Unit> getUnitAt(Location loc) {
		return units.stream().filter(u -> u.getX() == loc.x && u.getY() == loc.y).findAny();
	}

	public Optional<Unit> getUnitAt(int x, int y) {
		return units.stream().filter(u -> u.getX() == x && u.getY() == y).findAny();
	}

	/**
	 *
	 * @param player
	 * @return all {@link Unit} of a player
	 */
	public List<Unit> playerUnits(PlayerColor player) {
		return units.stream()
				.filter(u -> u.getPlayer() == player)
				.collect(Collectors.toList());
	}

	/**
	 *
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

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public GameMap(String data) {
		
	}
}
