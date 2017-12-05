package game;

import game.enums.BuildingType;
import game.enums.PlayerColor;

public class Building {
	private int x, y;
	private BuildingType type;
	private PlayerColor color;

	public Building(int x, int y, BuildingType type, PlayerColor color) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.color = color;
	}

	public BuildingType getType() {return type;}

	public int getX() {return x;}

	public int getY() {return y;}

	public PlayerColor getPlayer() {return color;}
}
