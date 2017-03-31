package client.window.view;

import client.Controller;
import client.components.TextButton;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewServerConnect extends View {

	private Window window;
	private Controller controller;

	private TextButton buttonConnect, buttonBackToMainMenu;
	private JTextField textFieldName, textFieldHostName, textFieldPort;

	private JPanel panel;
	private DynamicBackground background;
	private boolean started = false;

	public ViewServerConnect(DynamicBackground background) {
		this.background = background;
	}

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		panel = window.getPanel();
		if(background == null) background = new DynamicBackground();
		window.getPanel().setLayout(null);

		buttonConnect = new TextButton("Connect", e -> window.updateView(new ViewGameSetup(background, textFieldName.getText(), textFieldHostName.getText(), Integer.valueOf(textFieldPort.getText()))));
		buttonBackToMainMenu = new TextButton("Back to Main Menu", e -> window.updateView(new ViewMainMenu(background)));
		textFieldName = new JTextField("[NAME]");
		textFieldHostName = new JTextField("localhost");
		textFieldPort = new JTextField("25565");

		window.getPanel().add(textFieldName);
		window.getPanel().add(textFieldHostName);
		window.getPanel().add(textFieldPort);
		window.getPanel().add(buttonConnect);
		window.getPanel().add(buttonBackToMainMenu);

		changeSize();

		started = true;
	}

	@Override
	public void changeSize() {
		int width = window.getPanel().getWidth();
		int height = window.getPanel().getHeight();

		int elementHeight = height/10;
		int elementWidth  = elementHeight*5;

		textFieldName.setBounds((width-elementWidth)/2, (height-5*elementHeight)/2, elementWidth, elementHeight);
		textFieldHostName.setBounds((width-elementWidth)/2, (height-3*elementHeight)/2, elementWidth, elementHeight);
		textFieldPort.setBounds((width-elementWidth)/2, (height-elementHeight)/2, elementWidth, elementHeight);
		buttonConnect.setBounds((width-elementWidth)/2, (height+2*elementHeight)/2, elementWidth, elementHeight);
		buttonBackToMainMenu.setBounds((width-elementWidth)/2, (height+5*elementHeight)/2, elementWidth, elementHeight);
	}

	@Override
	public boolean autoDraw() {
		return true;
	}

	@Override
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
}
