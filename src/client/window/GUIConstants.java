package client.window;

import java.awt.*;

public class GUIConstants {
	public static final Color COLOR_GAME_BACKGROUND 		= new Color(135, 206, 235);
	public static final Color COLOR_INFOBAR_BACKGROUND 		= Color.DARK_GRAY;

	public static final int MAXIMUM_DRAG_DISTANCE_FOR_CLICK	= 10;

	public static final double UNIT_XY_RATIO				= 1;
	public static final double HEX_TILE_XY_RATIO			= 1.4/1.2;
	public static final double HEX_TILE_YY_RATIO			= 1.05/1.4;
	public static final double ZOOM							= 1.1;
  
	public static final double ARROW_SIZE 					= 0.5;

	public static Polygon HEX_TILE(int x, int y) {
		return new Polygon(new int[]{x, x + 60, x + 120, x + 120, x + 60, x}, new int[]{y + 35, y, y + 35, y + 105, y + 140, y + 105}, 6);
	}
  
	public static final Font FONT							= new Font("Arial", Font.BOLD, 12);

	public static final double BUTTON_HOVER_SIZE			= 0.9;
	public static final int BUTTON_LINE_WIDTH				= 5;
	public static final Color BUTTON_COLOR					= Color.WHITE;
	public static final int CORNER_RADIUS					= 30;

	public static Object VALUE_ANTIALIASING 				= RenderingHints.VALUE_ANTIALIAS_ON;
	public static String LAST_USERNAME						= null;
	public static String LAST_IP							= null;
	public static String LAST_PORT							= null;
}
