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

	private TextButton button_quit, button_start, button_create;

	private JPanel panel;
	private DynamicBackground background;

	private boolean started = false;

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

		panel.setLayout(null);

		button_quit = new TextButton("Quit Game", e -> System.exit(0));
		button_start = new TextButton("Join Game", e -> window.updateView(new ViewServerConnect(background)));
		button_create = new TextButton("Create Game", e -> window.updateView(new ViewServerCreate(background)));

		changeSize();

		panel.add(button_start);
		panel.add(button_quit);
		panel.add(button_create);

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
		int width = window.getPanel().getWidth();
		int height = window.getPanel().getHeight();

		int buttonHeight = height/8;
		int buttonWidth = buttonHeight*5;

		button_create.setBounds((width-buttonWidth)/2, (height-buttonHeight)/2, buttonWidth, buttonHeight);
		button_start.setBounds((width-buttonWidth)/2, (height+2*buttonHeight)/2, buttonWidth, buttonHeight);
		button_quit.setBounds((width-buttonWidth)/2, (height+5*buttonHeight)/2, buttonWidth, buttonHeight);
	}

	/**
	 * Draws this screen
	 */
	public void draw() {
		if (!started) return;

		BufferedImage buffer = background.draw(panel.getWidth(), panel.getHeight());

		Graphics g = buffer.getGraphics();

		for (Component component: panel.getComponents()) {
			g.translate(component.getX(), component.getY());
			component.update(g);
			g.translate(-component.getX(), -component.getY());
		}

		panel.getGraphics().drawImage(buffer, 0, 0, null);
	}

	@Override
	public void stop() {
		started = false;
	}
}