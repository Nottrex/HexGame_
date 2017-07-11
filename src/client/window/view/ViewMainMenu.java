package client.window.view;

import client.Options;
import client.game.Controller;
import client.i18n.LanguageHandler;
import client.window.TextureHandler;
import client.window.Window;
import client.window.components.ImageButton;
import client.window.components.TextButton;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewMainMenu extends View {

	private Window window;
	private Controller controller;

	private TextButton button_quit, button_start, button_create;
	private ImageButton button_options;

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

		if (background == null) background = new DynamicBackground();

		panel = window.getPanel();

		panel.setLayout(null);

		TextureHandler.loadImagePng("Options", "ui/buttons/options");

		button_quit = new TextButton(window, LanguageHandler.get("Exit Game"), e -> {
			Options.save();
			System.exit(0);
		});
		button_start = new TextButton(window, LanguageHandler.get("Join Game"), e -> window.updateView(new ViewServerConnect(background)));
		button_create = new TextButton(window, LanguageHandler.get("Create Game"), e -> window.updateView(new ViewServerCreate(background)));
		button_options = new ImageButton(window, TextureHandler.getImagePng("Options"), e -> window.updateView(new ViewOptions(window, background, this)));

		changeSize();

		panel.add(button_start);
		panel.add(button_quit);
		panel.add(button_create);
		panel.add(button_options);

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
		if (button_create == null || button_start == null || button_quit == null || button_options == null) return;

		int width = window.getPanel().getWidth();
		int height = window.getPanel().getHeight();

		int buttonHeight = height / 8;
		int buttonWidth = buttonHeight * 5;

		button_create.setBounds((width - buttonWidth) / 2, (height - buttonHeight) / 2, buttonWidth, buttonHeight);
		button_start.setBounds((width - buttonWidth) / 2, (height + 2 * buttonHeight) / 2, buttonWidth, buttonHeight);
		button_quit.setBounds((width - buttonWidth) / 2, (height + 5 * buttonHeight) / 2, buttonWidth, buttonHeight);
		button_options.setBounds(5, 5, 2 * buttonHeight / 3, 2 * buttonHeight / 3);
	}

	/**
	 * Draws this screen
	 */
	public void draw() {
		if (!started) return;

		BufferedImage buffer = background.draw(panel.getWidth(), panel.getHeight());

		Graphics g = buffer.getGraphics();

		for (Component component : panel.getComponents()) {
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