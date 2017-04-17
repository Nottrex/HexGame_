package client.window.view;

import client.window.GUIConstants;
import game.Location;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DynamicBackground {
	private Map<Location, Color> drawOvers;
	private Map<Location, Color> targetColors;
	private Map<Location, Long>	 interpolationStart;
	private Map<Location, Long>  interpolationTime;
	private Color startColorCopy;
	private Color interpolationColor;

	private static final long COLOR_SWAP_TIME = 10000;
	private static final int MAX_COLOR_DISTANCE_BACKGROUND	= 128;
	private static final int MIN_SWAP_TIME	= 3000;
	private static final int MAX_SWAP_TIME = 6000;
	private static final int MAX_COLOR_BRIGHTNESS = 150;
	private long lastSwap = 0L;

	private Random r;

	private int w, h;

	public DynamicBackground() {
		drawOvers = new HashMap<>();
		targetColors = new HashMap<>();
		interpolationStart = new HashMap<>();
		interpolationTime = new HashMap<>();

		r = new Random();
		startColorCopy = new Color(r.nextInt(MAX_COLOR_BRIGHTNESS), r.nextInt(MAX_COLOR_BRIGHTNESS), r.nextInt(MAX_COLOR_BRIGHTNESS));
		interpolationColor = new Color(r.nextInt(MAX_COLOR_BRIGHTNESS), r.nextInt(MAX_COLOR_BRIGHTNESS), r.nextInt(MAX_COLOR_BRIGHTNESS));
	}

	/**
	 * Draws the background
	 * @param width of the image
	 * @param height of the image
	 * @return the finished background image
	 */
	public BufferedImage draw(int width, int height) {
		this.w = width;
		this.h = height;
		update();

		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) buffer.getGraphics();

		long currentTime = System.currentTimeMillis();

		g.setColor(interpolateColor(startColorCopy, interpolationColor, (1.0*(currentTime-lastSwap)) / COLOR_SWAP_TIME));
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

		for(Location l: drawOvers.keySet()) {
			Color c = interpolateColor(drawOvers.get(l), targetColors.get(l), (1.0*(currentTime-interpolationStart.get(l)))/interpolationTime.get(l));
			drawHexField(l.x, l.y, g, new Color(c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/255.0f, 0.2f));
		}

		BufferedImage buffer2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		float data[] = { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f,
				0.0625f, 0.125f, 0.0625f };
		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
				null);
		convolve.filter(buffer, buffer2);

		return buffer2;
	}

	/**
	 * Calculates all new colors of the hexagons and the background color
	 */
	private void update() {
		long currentTime = System.currentTimeMillis();


		for(int x = -1; x < w/120 + 15; x++){
			for(int y = -1; y < h/140 + 18; y++){
				Location l = new Location(x, y);
				if(!drawOvers.containsKey(l)){
					int gr = r.nextInt(255);
					l = new Location(x, y);
					drawOvers.put(l, new Color(gr, gr, gr));

					int gr2 = gr+(r.nextInt(2*MAX_COLOR_DISTANCE_BACKGROUND+1)-MAX_COLOR_DISTANCE_BACKGROUND);

					if (gr2 < 0) gr2 = 0;
					if (gr2 > 254) gr2 = 254;
					targetColors.put(l, new Color(gr2, gr2, gr2));

					interpolationStart.put(l, currentTime);
					interpolationTime.put(l, (long) (r.nextInt((MAX_SWAP_TIME-MIN_SWAP_TIME))+MIN_SWAP_TIME));
				}
			}
		}

		for (Location l: drawOvers.keySet()) {
			if (currentTime - interpolationStart.get(l) > interpolationTime.get(l)) {
				int gr = drawOvers.get(l).getRed();

				drawOvers.put(l, targetColors.get(l));

				int gr2 = gr+(r.nextInt(2*MAX_COLOR_DISTANCE_BACKGROUND+1)-MAX_COLOR_DISTANCE_BACKGROUND);

				if (gr2 < 0) gr2 = 0;
				if (gr2 > 254) gr2 = 254;
				targetColors.put(l, new Color(gr2, gr2, gr2));

				interpolationStart.put(l, currentTime);
				interpolationTime.put(l, (long) (r.nextInt((MAX_SWAP_TIME-MIN_SWAP_TIME))+MIN_SWAP_TIME));
			}
		}

		if(currentTime - lastSwap >= COLOR_SWAP_TIME) {
			startColorCopy = interpolationColor;
			interpolationColor = new Color(r.nextInt(MAX_COLOR_BRIGHTNESS), r.nextInt(MAX_COLOR_BRIGHTNESS), r.nextInt(MAX_COLOR_BRIGHTNESS));
			lastSwap = currentTime;
		}
	}

	/**
	 *
	 * @param start Color at the start on the interpolation
	 * @param end Wanted Color
	 * @param d Last time in relation to total time of one interpolation zyklus
	 * @return The interpolated color
	 */
	private Color interpolateColor(Color start, Color end, double d) {
		if (d < 0) d = 0;
		if (d > 1) d = 1;
		d = -2*d*d*d+3*d*d;
		int red = (int) (d * end.getRed() + start.getRed()*(1-d));
		int green = (int) (d * end.getGreen() + start.getGreen()*(1-d));
		int blue = (int) (d * end.getBlue() + start.getBlue()*(1-d));
		return new Color(red, green, blue);
	}

	/**
	 * Draws a hexagon on a position
	 * @param x position of the hexagon
	 * @param y position of the hexagon
	 * @param g Graphics used for drawing
	 * @param c Color of the hexagon
	 */
	private void drawHexField(int x, int y, Graphics g, Color c) {
		double py = (y)*(GUIConstants.HEX_TILE_YY_RATIO)*140;
		double px = (x)*120 - (y)*60;

		g.setColor(c);
		g.translate((int)px, (int)py);

		g.fillPolygon(GUIConstants.HEX_TILE);		// The part {0, 60, 120}{35, 0, 35} is the problem which uses all of the memory

		g.translate(-(int)px, -(int)py);
	}
}
