package client.window.view;

import client.Controller;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;

public class ViewServerConnect extends View {

	private Window window;
	private Controller controller;

	private JButton buttonConnect, buttonBackToMainMenu;
	private JTextField textFieldName, textFieldHostName, textFieldPort;

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		window.getPanel().setLayout(new FlowLayout());

		buttonConnect = new JButton("Connect");
		buttonBackToMainMenu = new JButton("Back to Main Menu");
		textFieldName = new JTextField("[NAME]");
		textFieldHostName = new JTextField("[HostName]");
		textFieldPort = new JTextField("[PORT]");

		window.getPanel().add(textFieldName);
		window.getPanel().add(textFieldHostName);
		window.getPanel().add(textFieldPort);
		window.getPanel().add(buttonConnect);
		window.getPanel().add(buttonBackToMainMenu);

		buttonBackToMainMenu.addActionListener(e -> window.updateView(new ViewMainMenu()));
		buttonConnect.addActionListener(e -> window.updateView(new ViewGameSetup(textFieldName.getText(), textFieldHostName.getText(), Integer.valueOf(textFieldPort.getText()))));
	}
}
