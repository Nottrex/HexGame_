package game.enums;

/**
 * A Direction on the Hexagon field
 */
public enum Direction {
	LEFT(-1, 0), RIGHT(1, 0), UP_LEFT(-1, -1), UP_RIGHT(0, -1), DOWN_LEFT(0, 1), DOWN_RIGHT(1, 1);
	
	private int mx, my;
	
	Direction(int mx, int my) {
		this.mx = mx;
		this.my = my;
	}
	
	public int getXMovement() {
		return mx;
	}
	
	public int getYMovement() {
		return my;
	}
}
