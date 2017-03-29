package client.window.view;

import client.Controller;
import client.window.GUIConstants;
import client.window.View;
import client.window.Window;
import game.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ViewMainMenu extends View {

	private Window window;
	private Controller controller;

	private JPanel drawCanvas;

	private Map<Location, Color> drawOvers;
	private Color startColor;

	private Long lastUpdate = 0L;

	private Random r;

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		r = new Random();
		drawOvers = new HashMap<>();

		drawCanvas = window.getPanel();

		r = new Random();
		startColor = new Color(r.nextInt());


	}

	@Override
	public boolean autoDraw() {
		return true;
	}

	@Override
	public void draw() {
		update();

		BufferedImage buffer = new BufferedImage(drawCanvas.getWidth(), drawCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = buffer.getGraphics();

		g.setColor(startColor);
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

		for(Location l: drawOvers.keySet()) {
			Color c = drawOvers.get(l);
			drawHexField(l.x, l.y, g, new Color(c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/255.0f, 0.2f));
		}

		drawCanvas.getGraphics().drawImage(buffer, 0, 0, null);
	}

	private void update() {
		Long currentTime = System.currentTimeMillis();

		if(currentTime - lastUpdate >= 500) {

			System.out.println("UPDA");

			int w = drawCanvas.getWidth();
			int h = drawCanvas.getHeight();

			for(int x = -1; x < w/120; x++){
				for(int y = -1; y < h/140; y++){
					if(!drawOvers.containsKey(new Location(x, y))) drawOvers.put(new Location(x, y), Color.GRAY);
				}
			}

			for(Location l: drawOvers.keySet()) {
				if(r.nextInt(10) < 3) {
					int mode = r.nextInt(3);
					for(int i = 0; i < r.nextInt(5); i++) {
						if(mode == 0) drawOvers.get(l).brighter();
						else if(mode == 1) drawOvers.get(l).darker();
					}
				}
			}

			lastUpdate = currentTime;
		}
	}

	private void drawHexField(int x, int y, Graphics g, Color c) {
		double py = (y)*(GUIConstants.HEX_TILE_YY_RATIO)*140;
		double px = (x)*120 - (y)*60;

		g.setColor(c);
		g.fillPolygon(GUIConstants.HEX_TILE((int)px, (int)py));
	}
}