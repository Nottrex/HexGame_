package client.window;

import client.window.view.ViewGame;
import javafx.scene.input.KeyCode;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyInputListener extends KeyAdapter {
	private ViewGame game;

	private HashMap<Integer, Boolean> pressed = new HashMap<>();

	public KeyInputListener(ViewGame game) {
		this.game = game;
	}

	/**
	 *
	 * @param i {@link KeyEvent KeyCode} of a char
	 * @return whether the related button is pressed or not
	 */
	private boolean isPressed(int i) {
		return pressed.containsKey(i) ? pressed.get(i) : false;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		pressed.put(e.getKeyCode(), false);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!isPressed(e.getKeyCode())) {
			game.onKeyType(e.getKeyCode());
		}

		pressed.put(e.getKeyCode(), true);
	}
}
