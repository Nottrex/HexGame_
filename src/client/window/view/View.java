package client.window.view;

import client.window.Window;
import client.game.Controller;

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
