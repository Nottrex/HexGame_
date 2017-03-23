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

	private int round;
	private int playerTurn;

	public Game() {
		map = new GameMap(25, 25);
		playerAmount = 2;
		player = new PlayerColor[]{PlayerColor.BLUE, PlayerColor.RED};
	}

	public void nextRound() {
		round++;
		playerTurn = 0;

		for (Unit u : map.getUnits()) {
			u.setActive(true);
		}
	}

	public void nextPlayer() {
		playerTurn++;
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

	public PlayerColor[] getPlayer() {
		return player;
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
