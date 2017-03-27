package client.window.view;

import client.Controller;
import client.window.View;
import client.window.Window;

public class ViewMainMenu implements View {

	private Window window;
	private Controller controller;

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;
	}

	@Override
	public boolean autoDraw() {
		return false;
	}

	@Override
	public void draw() {

	}

	@Override
	public void stop() {

	}


}
