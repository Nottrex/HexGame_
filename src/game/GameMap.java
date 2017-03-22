package game;

import game.enums.Field;

public class GameMap {
	private Field[][] map;
	private int width, height;

	public GameMap(int width, int height) {
		map = new Field[width][height];
		this.width = width;
		this.height = height;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				map[x][y] = Field.values()[(int) (Math.random()*Field.values().length)];
			}
		}
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
