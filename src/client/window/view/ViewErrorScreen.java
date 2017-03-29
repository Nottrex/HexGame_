package client.window.view;

import client.Controller;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewErrorScreen extends View {

	private Window window;
	private Controller controller;

	private String error;
	private JLabel labelError;
	private JButton buttonBackToMainMenu;

	private JPanel panel;
	private DynamicBackground background;

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
		panel.setLayout(new FlowLayout());

		if(background == null) background = new DynamicBackground();

		labelError = new JLabel(error);
		buttonBackToMainMenu = new JButton("Back to Main Menu");

		window.getPanel().add(labelError);
		window.getPanel().add(buttonBackToMainMenu);

		buttonBackToMainMenu.addActionListener(e -> window.updateView(new ViewMainMenu(background)));
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
