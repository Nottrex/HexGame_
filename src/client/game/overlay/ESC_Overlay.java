package client.game.overlay;

import client.game.ViewGame;
import client.i18n.LanguageHandler;
import client.window.GUIConstants;
import client.window.Window;
import client.window.components.TextButton;
import client.window.view.ViewMainMenu;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class ESC_Overlay extends Overlay {

	private Window w;
	private ViewGame game;

	private TextButton button_BackToGame;
	private TextButton button_ToOptions;
	private TextButton button_quit;

	public ESC_Overlay(Window w, ViewGame g) {
		this.w = w;
		this.game = g;

		button_BackToGame = new TextButton(w, LanguageHandler.get("Back"), e -> {
			g.setOverlay(null);
			g.unhideButtons();
		});
		this.add(button_BackToGame);

		button_ToOptions = new TextButton(w, LanguageHandler.get("Options"), e -> {
			g.setOverlay(new OptionsOverlay(w, g));
		});
		this.add(button_ToOptions);

		button_quit = new TextButton(w, LanguageHandler.get("Back to Mainmenu"), e -> {
			w.updateView(new ViewMainMenu());
		});
		this.add(button_quit);
		changeSize();

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		this.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
			}
		});
	}

	@Override
	public boolean destroyable() {
		return false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(GUIConstants.COLOR_OVERLAY_BACKGROUND);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	public void changeSize() {
		if (button_ToOptions == null || button_BackToGame == null) return;

		int width = w.getPanel().getWidth();
		int height = w.getPanel().getHeight() - game.getBottomHeigth();
		setBounds(0, 0, width, height);

		int buttonHeight = height / 8;
		int buttonWidth = buttonHeight * 5;

		button_BackToGame.setBounds((width - buttonWidth) / 2, (height - buttonHeight) / 4, buttonWidth, buttonHeight);
		button_ToOptions.setBounds((width - buttonWidth) / 2, (height + 4 * buttonHeight) / 4, buttonWidth, buttonHeight);
		button_quit.setBounds((width - buttonWidth) / 2, (height + 9 * buttonHeight) / 4, buttonWidth, buttonHeight);
	}
}