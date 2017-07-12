package client.window;

import java.awt.*;

public class GUIConstants {
	public static final Color COLOR_GAME_BACKGROUND = new Color(135, 206, 235);
	public static final Color COLOR_INFOBAR_BACKGROUND = Color.DARK_GRAY;
	public static final Color COLOR_OVERLAY_BACKGROUND = new Color(0, 0, 0, 100);

	public static final int MAXIMUM_DRAG_DISTANCE_FOR_CLICK = 10;

	public static final double UNIT_XY_RATIO = 1.4 / 1.2;
	public static final double HEX_TILE_XY_RATIO = 1.4 / 1.2;
	public static final double HEX_TILE_YY_RATIO = 1.05 / 1.4;
	public static final double ZOOM = 1.1;

	public static final float UNIT_SPAWN_RANGE = 0.075f;

	public static final float MIN_TILT = -0.5f;
	public static final float MAX_TILT = 0.5f;
	public static final float TILT_STEP = 0.1f;
	public static final long CAMERA_TIME = 500;
	public static final long ZOOM_TIME = 200;

	public static final double ARROW_SIZE = 0.5;

	public static final Polygon HEX_TILE = new Polygon(new int[]{0, 60, 120, 120, 60, 0}, new int[]{35, 0, 35, 105, 140, 105}, 6);

	public static final Font FONT = new Font("Arial", Font.BOLD, 12);

	public static final double BUTTON_HOVER_SIZE = 0.9;
	public static final int BUTTON_LINE_WIDTH = 5;
	public static final Color BUTTON_COLOR = Color.WHITE;
	public static final int CORNER_RADIUS = 30;

	public static final int MAX_MAP_SIZE = 2000;
}
