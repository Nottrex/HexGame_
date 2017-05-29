package client.window.view;

import client.FileHandler;
import client.game.Controller;
import client.window.components.HorizontalSlider;
import client.window.components.TextButton;
import client.window.components.TextLabel;
import client.window.GUIConstants;
import client.window.Window;
import game.map.presets.*;
import client.i18n.LanguageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewServerOptions extends View {

	private Window window;
	private ViewServerCreate prev;
	private DynamicBackground background;

	private HorizontalSlider mapWidth, mapHeight;
	private TextLabel displayWidth, displayHeight;
	private int widthValue, heightValue;

	private TextButton accept, cancel;

	private TextButton buttonMapType;
	private String mapType;

	private boolean started;

	public ViewServerOptions (Window window, ViewServerCreate pre, DynamicBackground background) {
		this.background = background;
		this.window = window;
		this.prev = pre;
	}

	@Override
	public void init(Window window, Controller controller) {
		mapType = LanguageHandler.get("Hexagonal");
		widthValue = calcMapVal(0.5d);
		heightValue = calcMapVal(0.5d);

		buttonMapType = new TextButton(window, mapType, e->{

			if(mapType.equals(LanguageHandler.get("Hexagonal"))) mapType = LanguageHandler.get("Oval");
			else if(mapType.equals(LanguageHandler.get("Oval"))) mapType = LanguageHandler.get("Square");
			else if(mapType.equals(LanguageHandler.get("Square"))) mapType = LanguageHandler.get("Custom");
			else if(mapType.equals(LanguageHandler.get("Custom"))) mapType = LanguageHandler.get("Hexagonal");

			System.out.println(mapType);
			buttonMapType.setText(mapType);
		});

		displayWidth = new TextLabel(new TextLabel.Text() {
			@Override
			public String getText() {
				return LanguageHandler.get("Width") + ": " + widthValue + "";
			}
		}, false);

		displayHeight = new TextLabel(new TextLabel.Text() {
			@Override
			public String getText() {
				return LanguageHandler.get("Height") + ": " + heightValue + "";
			}
		}, false);

		mapWidth = new HorizontalSlider(0.5f, e -> widthValue = calcMapVal(mapWidth.getValue()));
		mapHeight = new HorizontalSlider(0.5f, e -> heightValue = calcMapVal(mapHeight.getValue()));

		accept = new TextButton(window, LanguageHandler.get("Accept"), e->{
			if(widthValue % 2 == 0) widthValue += 1;
			if(heightValue % 2 == 0) heightValue += 1;

			MapPreset mp = null;
			if(mapType.equals(LanguageHandler.get("Hexagonal"))) mp = new HexPreset((widthValue + heightValue)/2);
			else if(mapType.equals(LanguageHandler.get("Oval"))) mp = new OvalPreset(widthValue, heightValue);
			else if(mapType.equals(LanguageHandler.get("Square"))) mp = new SquarePreset(widthValue, heightValue);
			else if(mapType.equals(LanguageHandler.get("Custom"))) mp = new CustomPreset(FileHandler.loadFile("maps/map.txt"));
			prev.setPreset(mp);
			window.updateView(prev);
		});

		cancel = new TextButton(window, LanguageHandler.get("Cancel"), e -> window.updateView(prev));

		window.getPanel().add(cancel);
		window.getPanel().add(accept);
		window.getPanel().add(mapWidth);
		window.getPanel().add(mapHeight);
		window.getPanel().add(displayHeight);
		window.getPanel().add(displayWidth);
		window.getPanel().add(buttonMapType);

		changeSize();

		started = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (started) {
					draw();
				}
			}
		}).start();
	}

	@Override
	public void changeSize() {
		int width = window.getWidth();
		int height = window.getHeight();

		int componentHeight = height/12;
		int componentWidth = componentHeight * 5;

		accept.setBounds(width/2 - componentWidth - 5, height - 2*componentHeight, componentWidth, componentHeight);
		cancel.setBounds(width/2 + 5, height - 2*componentHeight, componentWidth, componentHeight);

		mapWidth.setBounds(5, 5 + componentHeight/4, componentWidth, componentHeight/2);
		displayWidth.setBounds(10 + componentWidth, 5, componentWidth, componentHeight);

		mapHeight.setBounds(5, 10 + componentHeight + componentHeight/4, componentWidth, componentHeight/2);
		displayHeight.setBounds(10 + componentWidth, 10 + componentHeight, componentWidth, componentHeight);

		buttonMapType.setBounds(5, 15 + 2*componentHeight, componentWidth, componentHeight);
	}

	public void draw() {
		if (!started) return;
		JPanel panel = window.getPanel();

		BufferedImage buffer = background.draw(panel.getWidth(), panel.getHeight());

		Graphics g = buffer.getGraphics();

		for (Component component: panel.getComponents()) {
			g.translate(component.getX(), component.getY());
			component.update(g);
			g.translate(-component.getX(), -component.getY());
		}

		panel.getGraphics().drawImage(buffer, 0, 0, null);
	}

	private int calcMapVal(double x) {
		return Math.round(((float) Math.pow(x, 2) * 0.975f + 0.025f) * GUIConstants.MAX_MAP_SIZE);
	}

	@Override
	public void stop() {
		started = false;
	}
}
