package client.window.view;

import client.Controller;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;

public class ViewErrorScreen implements View {

	private Window window;
	private Controller controller;

	private String error;
	private JLabel labelError;
	private JButton buttonBackToMainMenu;

	public ViewErrorScreen(String error) {
		this.error = error;
	}

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		window.getPanel().setLayout(new FlowLayout());

		labelError = new JLabel(error);
		buttonBackToMainMenu = new JButton("Back to Main Menu");

		window.getPanel().add(labelError);
		window.getPanel().add(buttonBackToMainMenu);

		buttonBackToMainMenu.addActionListener(e -> window.updateView(new ViewMainMenu()));
	}

	@Override
	public boolean autoDraw() {
		return false;
	}

	@Override
	public void draw() {

	}

	@Override
	public void stop() {

	}
}
