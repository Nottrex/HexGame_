package client.window.view;

import client.Controller;
import client.components.TextButton;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewMainMenu extends View {

	private Window window;
	private Controller controller;

	private TextButton button_quit, button_start;

	private JPanel panel;
	private DynamicBackground background;

	public ViewMainMenu() {

	}

	public ViewMainMenu(DynamicBackground background) {
		this.background = background;
	}

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		if(background == null) background = new DynamicBackground();

		panel = window.getPanel();

		panel.setLayout(new FlowLayout());

		button_quit = new TextButton("Quit Game", e -> System.exit(0));
		button_start = new TextButton("Start", e -> window.updateView(new ViewServerConnect(background)));

		panel.add(button_start);
		panel.add(button_quit);
	}

	@Override
	public boolean autoDraw() {
		return true;
	}

	@Override
	public void draw() {
		BufferedImage buffer = background.draw(panel.getWidth(), panel.getHeight());

		Graphics g = buffer.getGraphics();

		for (Component component: panel.getComponents()) {
			g.translate(component.getX(), component.getY());
			component.update(g);
			g.translate(-component.getX(), -component.getY());
		}

		panel.getGraphics().drawImage(buffer, 0, 0, null);
	}


}