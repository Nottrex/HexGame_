package game;

import game.util.MapUtil;

public class Location {
	public int x;
	public int y;

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + x;
		hash = hash * 31 + y;

		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location b = (Location) obj;
			return x == b.x && y == b.y;
		}
		return false;
	}

	public int distanceTo(Location loc2) {
		return MapUtil.getDistance(x, y, loc2.x, loc2.y);
	}

	@Override
	public String toString() {
		return String.format("(%d | %d)", x, y);
	}
}
