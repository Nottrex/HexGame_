package view;

import java.awt.event.*;

public class MouseInputListener extends MouseAdapter {

	private Window window;

	private boolean mousePressedInGame = false;
	private int lastX = 0, lastY = 0;
	private int mouseX = 0, mouseY = 0;
	private int totalDistanceDragged = 0;

	public MouseInputListener(Window window) {
		this.window = window;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		window.onMouseWheel(e.getScrollAmount() * e.getPreciseWheelRotation());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//Update dragging
		if (mousePressedInGame) {
			totalDistanceDragged += Math.abs(e.getX()-lastX) + Math.abs(e.getY()-lastY);
			window.onMouseDrag(e.getX() - lastX, e.getY() - lastY);
			lastX = e.getX();
			lastY = e.getY();
		}

		//Update mouse move
		int x = e.getX() - window.i.left;
		int y = e.getY() - window.i.top;
		if (x >= window.center.getX() && x < (window.center.getX() + window.center.getWidth()) && y >= window.center.getY() && y < (window.center.getY() + window.center.getHeight())) {
			mouseX = x-window.center.getX();
			mouseY = y-window.center.getY();
			window.redrawInfoBar();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX() - window.i.left;
		int y = e.getY() - window.i.top;
		if (x >= window.center.getX() && x < (window.center.getX() + window.center.getWidth()) && y >= window.center.getY() && y < (window.center.getY() + window.center.getHeight())) {
			mouseX = x-window.center.getX();
			mouseY = y-window.center.getY();
			window.redrawInfoBar();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX() - window.i.left;
		int y = e.getY() - window.i.top;
		if (x >= window.center.getX() && x < (window.center.getX() + window.center.getWidth()) && y >= window.center.getY() && y < (window.center.getY() + window.center.getHeight())) {
			window.onMouseClick(x - window.center.getX(), y - window.center.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX() - window.i.left;
		int y = e.getY() - window.i.top;
		if (x >= window.center.getX() && x < (window.center.getX() + window.center.getWidth()) && y >= window.center.getY() && y < (window.center.getY() + window.center.getHeight())) {
			mousePressedInGame = true;
			lastX = e.getX();
			lastY = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//When the cam was moved less then "MAXIMUM_DRAG_DISTANCE_FOR_CLICK" then it should still count as a click
		if (mousePressedInGame && totalDistanceDragged > 0) {
			if (totalDistanceDragged <= Constants.MAXIMUM_DRAG_DISTANCE_FOR_CLICK) {
				int x = e.getX() - window.i.left;
				int y = e.getY() - window.i.top;
				if (x >= window.center.getX() && x < (window.center.getX() + window.center.getWidth()) && y >= window.center.getY() && y < (window.center.getY() + window.center.getHeight())) {
					window.onMouseClick(x - window.center.getX(), y - window.center.getY());
				}
			}

			totalDistanceDragged = 0;
			mousePressedInGame = false;
		}
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}
}
