public class Location {
	public int x, y;

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public Object clone() {
		return new Location(x, y);
	}

	@Override
	public boolean equals(Object o) {
		Location l = (Location) o;
		return l.x == x && l.y == y;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash*31 + x;
		hash = hash*31 + y;

		return hash;
	}

	@Override
	public String toString() {
		return String.format("(%d | %d)", x, y);
	}

}