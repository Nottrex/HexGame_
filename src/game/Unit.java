package game;

import game.enums.PlayerColor;
import game.enums.UnitState;
import game.enums.UnitType;

public class Unit {
	private PlayerColor player;
	private UnitType type;
	private UnitState state;
	private int x, y;

	public Unit(PlayerColor player, UnitType type, int x, int y) {
		this.player = player;
		this.type = type;
		this.state = UnitState.ACTIVE;
		this.x = x;
		this.y = y;
	}

	public Unit(PlayerColor player, UnitType type, UnitState state, int x, int y) {
		this.player = player;
		this.type = type;
		this.state = state;
		this.x = x;
		this.y = y;
	}

	public PlayerColor getPlayer() {
		return player;
	}

	public void setPlayer(PlayerColor player) {
		this.player = player;
	}

	public UnitType getType() {
		return type;
	}

	public void setType(UnitType type) {
		this.type = type;
	}

	public UnitState getState() {
		return state;
	}

	public void setState(UnitState state) {
		this.state = state;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
