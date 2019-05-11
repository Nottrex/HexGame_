package client.game;

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
	 * @param i {@link KeyEvent KeyCode} of a char
	 * @return whether the related button is pressed or not
	 */
	public boolean isPressed(int i) {
		return pressed.getOrDefault(i, false);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		pressed.put(e.getKeyCode(), false);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!isPressed(e.getKeyCode())) {
			new Thread(() -> game.onKeyType(e.getKeyCode())).start();
		}

		pressed.put(e.getKeyCode(), true);
	}
}
