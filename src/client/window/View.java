package client.window;

import client.Controller;

public abstract class View {
	public abstract void init(Window window, Controller controller);

	public boolean autoDraw() {
		return false;
	}

	public void draw() {

	}

	public void stop() {

	}
}
