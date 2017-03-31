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
	private Color startColor;
	private Color startColorCopy;
	private Color interpolationColor;

	private static final long COLOR_SWAP_TIME = 10000;
	private long lastUpdate = 0L;
	private long lastSwap = 0L;

	private Random r;

	private int w, h;

	public DynamicBackground() {
		drawOvers = new HashMap<>();

		r = new Random();
		startColor = new Color(r.nextInt(150), r.nextInt(150), r.nextInt(150));
		startColorCopy = new Color(startColor.getRGB());
		interpolationColor = new Color(r.nextInt(150), r.nextInt(150), r.nextInt(150));
		lastUpdate = System.currentTimeMillis();
	}

	public BufferedImage draw(int width, int height) {
		this.w = width;
		this.h = height;
		update();

		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) buffer.getGraphics();

		g.setColor(startColor);
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

		for(Location l: drawOvers.keySet()) {
			Color c = drawOvers.get(l);
			drawHexField(l.x, l.y, g, new Color(c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/255.0f, 0.2f));
		}

		return buffer;
	}

	public Color getCurrentColor() {
		return startColor;
	}

	private void update() {
		long currentTime = System.currentTimeMillis();

		while(currentTime - lastUpdate >= 75) {

			for(int x = -1; x < w/120 + 15; x++){
				for(int y = -1; y < h/140 + 18; y++){
					if(!drawOvers.containsKey(new Location(x, y))){
						int gr = r.nextInt(255);
						drawOvers.put(new Location(x, y), new Color(gr, gr, gr));
					}
				}
			}

			for(Location l: drawOvers.keySet()) {
				if(r.nextInt(10) < 3) {
					int mode = r.nextInt(3);
					if(mode == 0) {
						drawOvers.put(l, brighter(drawOvers.get(l)));
					}
					else if(mode == 1) drawOvers.put(l, darker(drawOvers.get(l)));
				}
			}

			lastUpdate += 75;
		}

		if(currentTime - lastSwap >= COLOR_SWAP_TIME) {
			startColor = interpolationColor;
			startColorCopy = new Color(startColor.getRGB());
			interpolationColor = new Color(r.nextInt(150), r.nextInt(150), r.nextInt(150));
			lastSwap = currentTime;
		}else {
			double faktor = (double)(currentTime - lastSwap) / (double)COLOR_SWAP_TIME;

			int red = (int) (faktor * interpolationColor.getRed() + startColorCopy.getRed()*(1-faktor));
			int green = (int) (faktor * interpolationColor.getGreen() + startColorCopy.getGreen()*(1-faktor));
			int blue = (int) (faktor * interpolationColor.getBlue() + startColorCopy.getBlue()*(1-faktor));
			startColor = new Color(red, green, blue);
		}

	}

	private Color darker(Color c) {
		return new Color(Math.max(c.getRed() - 5, 0), Math.max(c.getGreen() - 5, 0), Math.max(c.getBlue() - 5, 0));
	}

	private Color brighter(Color c) {
		return new Color(Math.min(c.getRed() + 5, 255), Math.min(c.getGreen() + 5, 255), Math.min(c.getBlue() + 5, 255));
	}

	private void drawHexField(int x, int y, Graphics g, Color c) {
		double py = (y)*(GUIConstants.HEX_TILE_YY_RATIO)*140;
		double px = (x)*120 - (y)*60;

		g.setColor(c);
		g.fillPolygon(GUIConstants.HEX_TILE((int)px, (int)py));
	}
}
