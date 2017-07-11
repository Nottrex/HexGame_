package client.window.view;

import client.game.Controller;
import client.window.Window;

public abstract class View {
	public abstract void init(Window window, Controller controller);

	/**
	 * Called when view is switching
	 */
	public void stop() {

	}

	/**
	 * Called when user resizes window
	 */
	public void changeSize() {

	}
}
