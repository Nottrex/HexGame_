package client.window.view;

import client.Controller;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;

public class ViewMainMenu implements View {

	private Window window;
	private Controller controller;

	private JButton buttonStartGame, buttonQuitGame;

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		window.getPanel().setLayout(new FlowLayout());

		buttonStartGame = new JButton("Start Game");
		buttonQuitGame = new JButton("Quit game");

		window.getPanel().add(buttonStartGame);
		window.getPanel().add(buttonQuitGame);

		buttonQuitGame.addActionListener(e -> System.exit(0));
		buttonStartGame.addActionListener(e -> window.updateView(new ViewGame()));

		window.getPanel().updateUI();
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
		window.getPanel().removeAll();
	}


}
