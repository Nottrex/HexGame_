package game.util;

public class MapUtil {

	public static int getDistance(int x1, int y1, int x2, int y2) {
		int distance = 0;

		if (y1 >= y2) {
			int c = y1;
			y1 = y2;
			y2 = c;

			c = x1;
			x1 = x2;
			x2 = c;
		}

		if (x1 >= x2) {
			distance += y2-y1;
			y2 = y1;
			distance += x1-x2;
			x2 = x1;
		} else {
			int dx = x2-x1;
			int dy = y2-y1;

			if (dy < dx) {
				distance += dy;
				x2 -= dy;
				y2 -= dy;

				distance += x2 - x1;
				x2 = x1;
			} else {
				distance += dx;
				x2 -= dx;
				y2 -= dx;
				distance += y2 - y1;
				y2 = y1;
			}
		}

		return distance;
	}
}
