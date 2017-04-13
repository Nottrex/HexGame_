package client.window.view;

import client.Controller;
import client.components.TextButton;
import client.components.TextLabel;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewErrorScreen extends View {

	private Window window;
	private Controller controller;

	private String error;
	private TextLabel labelError;
	private TextButton buttonBackToMainMenu;

	private JPanel panel;
	private DynamicBackground background;
	private boolean started = false;

	public ViewErrorScreen(String error) {
		this.error = error;
	}
	public ViewErrorScreen(DynamicBackground background, String error) {
		this.background = background;
		this.error = error;
	}

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		panel = window.getPanel();
		panel.setLayout(null);

		if(background == null) background = new DynamicBackground();

		buttonBackToMainMenu = new TextButton(window, "Back to Main Menu", e -> window.updateView(new ViewMainMenu(background)));
		labelError = new TextLabel(() -> error, true);

		changeSize();

		window.getPanel().add(labelError);
		window.getPanel().add(buttonBackToMainMenu);

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

		int componentHeight = height/8;
		int componentWidth = componentHeight * 5;

		buttonBackToMainMenu.setBounds((width-componentWidth)/2, (height+componentHeight)/2, componentWidth, componentHeight);
		labelError.setBounds((width-componentWidth)/2, (height-componentHeight)/2, componentWidth, componentHeight);
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