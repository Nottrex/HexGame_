package game;

import game.enums.PlayerColor;
import game.enums.UnitState;
import game.map.GameMap;
import game.map.MapGenerator;

import java.util.HashMap;
import java.util.Map;

public class Game {
	public static final String VERSION = "0.3";

	private GameMap map;
	private int playerAmount;
	private Map<String, PlayerColor> players;
	private Map<PlayerColor, Integer> money;

	private int round;
	private int playerTurn;

	public Game(GameMap map, Map<String, PlayerColor> players, int round, int playerTurn) {
		this(map, players);

		this.round = round;
		this.playerTurn = playerTurn - 1;
	}

	public Game(GameMap map, Map<String, PlayerColor> players) {
		this.map = map;
		map.setGame(this);
		playerAmount = players.keySet().size();
		this.players = players;
		this.round = 1;
		this.money = new HashMap<>();
		for (PlayerColor p : players.values()) {
			money.put(p, 500);
		}
	}

	public Game(MapGenerator generator, Map<String, PlayerColor> players) {
		this.map = new GameMap(generator);
		map.setGame(this);
		this.players = players;
		playerAmount = players.keySet().size();
		this.round = 1;
		this.money = new HashMap<>();
		for (PlayerColor p : players.values()) {
			money.put(p, 500);
		}
	}

	public Game(String data) {

	}

	public void nextRound() {
		round++;
		playerTurn = 0;
		for (PlayerColor p : players.values()) {
			money.put(p, money.get(p) + 100);
		}
	}

	public void nextPlayer() {
		playerTurn++;
		for (Unit u : map.getUnits()) {
			u.setState(UnitState.ACTIVE);
		}

		if (playerTurn >= playerAmount) {
			nextRound();
		}
	}

	public GameMap getMap() {
		return map;
	}

	public int getPlayerAmount() {
		return playerAmount;
	}

	public Map<String, PlayerColor> getPlayers() {
		return players;
	}

	public int getRound() {
		return round;
	}

	public PlayerColor getPlayerColor() {
		return players.get(getPlayerTurn());
	}

	public String getPlayerTurn() {
		return (String) players.keySet().toArray()[playerTurn];
	}

	public int getPlayerTurnID() {
		return playerTurn + 1;
	}

	public int getPlayerID(PlayerColor pc) {
		for (String player : players.keySet()) {
			if (players.get(player) == pc) return getPlayerID(player);
		}
		return -1;
	}

	public int getPlayerID(String player) {
		Object[] pl = players.keySet().toArray();
		for (int i = 0; i < pl.length; i++) {
			if (pl[i].equals(player)) {
				return i + 1;
			}
		}
		return -1;
	}

	public int getPlayerMoney(PlayerColor color) {
		return money.get(color);
	}

	public void editPlayerMoney(PlayerColor color, int newMoney) {
		money.put(color, money.get(color) + newMoney);
	}

	public String save() {
		return null;
	}
}
