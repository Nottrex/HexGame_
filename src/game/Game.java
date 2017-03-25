package game;

import game.enums.PlayerColor;
import game.enums.UnitState;
import game.map.GameMap;
import game.map.MapGenerator;
import game.map.presets.HexPreset;
import game.map.presets.SquarePreset;

public class Game {

	private GameMap map;
	private int playerAmount;
	private PlayerColor[] players;

	private int round;
	private int playerTurn;

	public Game() {
		map = new GameMap(new MapGenerator(new HexPreset(51, 51)));
		playerAmount = 2;
		players = new PlayerColor[]{PlayerColor.BLUE, PlayerColor.RED};
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

	public PlayerColor[] getPlayers() {
		return players;
	}

	public int getRound() {
		return round;
	}

	public PlayerColor getPlayerTurn() {
		return players[playerTurn];
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
