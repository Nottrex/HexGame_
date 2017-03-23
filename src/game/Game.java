package game;

import game.enums.PlayerColor;
import game.enums.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Game {

	private GameMap map;
	private int playerAmount;
	private PlayerColor[] player;
	private List<Unit> units;

	private int round;
	private int playerTurn;

	public Game() {
		map = new GameMap(25, 25);
		units = new ArrayList<>();
		units.add(new Unit(PlayerColor.BLUE, UnitType.TANK, 20, 20));
		units.add(new Unit(PlayerColor.BLUE, UnitType.TANK, 15, 15));
		units.add(new Unit(PlayerColor.RED, UnitType.TANK, 20, 15));
		units.add(new Unit(PlayerColor.RED, UnitType.TANK, 5, 15));
		playerAmount = 2;
		player = new PlayerColor[]{PlayerColor.BLUE, PlayerColor.RED};
	}

	public void nextRound() {
		round++;
		playerTurn = 0;

		for (Unit u : units) {
			u.setActive(true);
		}
	}

	public void nextPlayer() {
		playerTurn++;
		if (playerTurn >= playerAmount) {
			nextRound();
		}
	}

	public Optional<Unit> getUnitAt(Location loc) {
		return units.stream().filter(u -> u.getX() == loc.x && u.getY() == loc.y).findAny();
	}

	public Optional<Unit> getUnitAt(int x, int y) {
		return units.stream().filter(u -> u.getX() == x && u.getY() == y).findAny();
	}

	public void moveUnitTo(Unit unit, int x, int y) {
		unit.setX(x);
		unit.setY(y);
	}

	public void attackUnit(Unit unit1, Unit unit2) {
		//TODO: Attacks/ Fights
	}

	public List<Unit> playerUnits(PlayerColor player) {
		return units.stream()
				.filter(u -> u.getPlayer() == player)
				.collect(Collectors.toList());
	}

	public List<Unit> activePlayerUnits(PlayerColor player) {
		return units.stream()
				.filter(u -> u.getPlayer() == player)
				.filter(u -> u.isActive())
				.collect(Collectors.toList());
	}

	public GameMap getMap() {
		return map;
	}

	public int getPlayerAmount() {
		return playerAmount;
	}

	public PlayerColor[] getPlayer() {
		return player;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public int getRound() {
		return round;
	}

	public PlayerColor getPlayerTurn() {
		return player[playerTurn];
	}

	public int getPlayerTurnID() {
		return playerTurn+1;
	}

	public String save() {
		return null;
	}

	public Game(String data) {

	}
}
