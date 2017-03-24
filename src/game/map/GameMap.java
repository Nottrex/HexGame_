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

	public GameMap(int width, int height) {
		map = new Field[width][height];
		this.width = width;
		this.height = height;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				map[x][y] = Field.values()[(int) (Math.random()*Field.values().length)];
			}
		}
		units = new ArrayList<>();
		units.add(new Unit(PlayerColor.BLUE, UnitType.TANK, 20, 20));
		units.add(new Unit(PlayerColor.BLUE, UnitType.TANK, 15, 15));
		units.add(new Unit(PlayerColor.RED, UnitType.TANK, 20, 15));
		units.add(new Unit(PlayerColor.RED, UnitType.TANK, 5, 15));
	}

	public GameMap(Field[][] map) {
		this.map = map;
		this.width = map.length;
		this.height = map[0].length;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				map[x][y] = Field.values()[(int) (Math.random()*Field.values().length)];
			}
		}
		units = new ArrayList<>();
		units.add(new Unit(PlayerColor.BLUE, UnitType.TANK, 20, 20));
		units.add(new Unit(PlayerColor.BLUE, UnitType.TANK, 15, 15));
		units.add(new Unit(PlayerColor.RED, UnitType.TANK, 20, 15));
		units.add(new Unit(PlayerColor.RED, UnitType.TANK, 5, 15));
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

	public List<Unit> playerUnits(PlayerColor player) {
		return units.stream()
				.filter(u -> u.getPlayer() == player)
				.collect(Collectors.toList());
	}

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

	public String save() {
		return null;
	}
	
	public GameMap(String data) {
		
	}
}
