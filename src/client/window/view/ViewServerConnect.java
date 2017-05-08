package client.window.view;

import client.Controller;
import client.components.CustomTextField;
import client.components.TextButton;
import client.window.GUIConstants;
import client.window.View;
import client.window.Window;
import i18n.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewServerConnect extends View {

	private Window window;
	private Controller controller;

	private TextButton buttonConnect, buttonBackToMainMenu;
	private CustomTextField textFieldName, textFieldHostName, textFieldPort;

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

		buttonConnect = new TextButton(window, Strings.get("Connect"),e ->
		{
			if (textFieldName.getText().isEmpty()) return;

			int port = -1;

			try {
				port = Integer.valueOf(textFieldPort.getText());
			} catch (Exception e2) {}

			GUIConstants.LAST_USERNAME = textFieldName.getText();
			GUIConstants.LAST_IP = textFieldHostName.getText();
			GUIConstants.LAST_PORT = textFieldPort.getText();

			window.updateView(new ViewGameSetup(null, background, textFieldName.getText(), textFieldHostName.getText(), port));
		});
		buttonBackToMainMenu = new TextButton(window, Strings.get("Back to Mainmenu"), e -> window.updateView(new ViewMainMenu(background)));

		textFieldName = new CustomTextField("Name", CustomTextField.KEY_RESTRICT_NORMAL);
		textFieldHostName = new CustomTextField("Hostname", CustomTextField.KEY_RESTRICT_NORMAL_OR_DOT);
		textFieldPort = new CustomTextField("Port", CustomTextField.KEY_RESTRICT_ONLY_DIGITS);

		if(GUIConstants.LAST_PORT != null) textFieldPort.setText(GUIConstants.LAST_PORT);
		if(GUIConstants.LAST_USERNAME != null) textFieldName.setText(GUIConstants.LAST_USERNAME);
		if(GUIConstants.LAST_IP != null) textFieldHostName.setText(GUIConstants.LAST_IP);

		changeSize();

		window.getPanel().add(textFieldName);
		window.getPanel().add(textFieldHostName);
		window.getPanel().add(textFieldPort);
		window.getPanel().add(buttonConnect);
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
