package client.window;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyInputListener extends KeyAdapter {
	private Window window;

	private HashMap<Integer, Boolean> pressed = new HashMap<>();

	public KeyInputListener(Window window) {
		this.window = window;
	}

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
			window.onKeyType(e.getKeyCode());
		}

		pressed.put(e.getKeyCode(), true);
	}
}
