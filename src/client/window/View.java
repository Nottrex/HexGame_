package client.window;

import client.Controller;

public interface View {
	public abstract void init(Window window, Controller controller);
	public abstract boolean autoDraw();
	public abstract void draw();
	public abstract void stop();
}
