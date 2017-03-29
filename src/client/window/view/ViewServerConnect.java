package client.window.view;

import client.Controller;
import client.components.TextButton;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;

public class ViewServerConnect extends View {

	private Window window;
	private Controller controller;

	private TextButton buttonConnect, buttonBackToMainMenu;
	private JTextField textFieldName, textFieldHostName, textFieldPort;

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		window.getPanel().setLayout(new FlowLayout());

		buttonConnect = new TextButton("Connect", e -> window.updateView(new ViewGameSetup(textFieldName.getText(), textFieldHostName.getText(), Integer.valueOf(textFieldPort.getText()))));
		buttonBackToMainMenu = new TextButton("Back to Main Menu", e -> window.updateView(new ViewMainMenu()));
		textFieldName = new JTextField("[NAME]");
		textFieldHostName = new JTextField("localhost");
		textFieldPort = new JTextField("25565");

		window.getPanel().add(textFieldName);
		window.getPanel().add(textFieldHostName);
		window.getPanel().add(textFieldPort);
		window.getPanel().add(buttonConnect);
		window.getPanel().add(buttonBackToMainMenu);
	}
}
