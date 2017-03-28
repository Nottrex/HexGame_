package game;

import game.enums.PlayerColor;
import game.enums.UnitState;
import game.map.GameMap;
import game.map.MapGenerator;
import game.map.presets.HexPreset;

import java.util.Map;

public class Game {
	public static final String VERSION = "0.2";

	private GameMap map;
	private int playerAmount;
	private Map<String, PlayerColor> players;

	private int round;
	private int playerTurn;

	public Game(GameMap map, Map<String, PlayerColor> players, int round, int playerTurn) {
		this(map, players);

		this.round = round;
		this.playerTurn = playerTurn-1;
	}

	public Game(GameMap map, Map<String, PlayerColor> players) {
		this.map = map;
		playerAmount = players.keySet().size();
		this.players = players;
	}

	public Game(int width, int height, Map<String, PlayerColor> players) {
		this.map = new GameMap(new MapGenerator(new HexPreset(width, height)));
		this.players = players;
	}

	public void nextRound() {
		round++;
		playerTurn = 0;
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
		return playerTurn+1;
	}

	public String save() {
		return null;
	}

	public Game(String data) {

	}
}
