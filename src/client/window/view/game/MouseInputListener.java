package client.window.view.game;

import client.window.GUIConstants;

import java.awt.event.*;

public class MouseInputListener extends MouseAdapter {

	private ViewGame game;

	private boolean mousePressedInGame = false;
	private int lastX = 0, lastY = 0;
	private int mouseX = 0, mouseY = 0;
	private int totalDistanceDragged = 0;

	public MouseInputListener(ViewGame game) {
		this.game = game;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (game.getCenter() == null) return;

		game.onMouseWheel(e.getScrollAmount() * e.getPreciseWheelRotation());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (game.getCenter() == null) return;

		//Update dragging
		if (mousePressedInGame) {
			totalDistanceDragged += Math.abs(e.getX()-lastX) + Math.abs(e.getY()-lastY);
			game.onMouseDrag(e.getX() - lastX, e.getY() - lastY);
			lastX = e.getX();
			lastY = e.getY();
		}

		//Update mouse move
		int x = e.getX();
		int y = e.getY();
		if (x >= game.getCenter().getX() && x < (game.getCenter().getX() + game.getCenter().getWidth()) && y >= game.getCenter().getY() && y < (game.getCenter().getY() + game.getCenter().getHeight())) {
			mouseX = x-game.getCenter().getX();
			mouseY = y-game.getCenter().getY();
			game.redrawInfoBar();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (game.getCenter() == null) return;

		int x = e.getX();
		int y = e.getY();
		if (x >= game.getCenter().getX() && x < (game.getCenter().getX() + game.getCenter().getWidth()) && y >= game.getCenter().getY() && y < (game.getCenter().getY() + game.getCenter().getHeight())) {
			mouseX = x-game.getCenter().getX();
			mouseY = y-game.getCenter().getY();
			game.redrawInfoBar();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (game.getCenter() == null) return;

		int x = e.getX();
		int y = e.getY();
		if (x >= game.getCenter().getX() && x < (game.getCenter().getX() + game.getCenter().getWidth()) && y >= game.getCenter().getY() && y < (game.getCenter().getY() + game.getCenter().getHeight())) {
			game.onMouseClick(x - game.getCenter().getX(), y - game.getCenter().getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (game.getCenter() == null) return;

		int x = e.getX();
		int y = e.getY();
		if (x >= game.getCenter().getX() && x < (game.getCenter().getX() + game.getCenter().getWidth()) && y >= game.getCenter().getY() && y < (game.getCenter().getY() + game.getCenter().getHeight())) {
			mousePressedInGame = true;
			lastX = e.getX();
			lastY = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (game.getCenter() == null) return;

		//When the cam was moved less then "MAXIMUM_DRAG_DISTANCE_FOR_CLICK" then it should still count as a click
		if (mousePressedInGame && totalDistanceDragged > 0) {
			if (totalDistanceDragged <= GUIConstants.MAXIMUM_DRAG_DISTANCE_FOR_CLICK) {
				int x = e.getX();
				int y = e.getY();
				if (x >= game.getCenter().getX() && x < (game.getCenter().getX() + game.getCenter().getWidth()) && y >= game.getCenter().getY() && y < (game.getCenter().getY() + game.getCenter().getHeight())) {
					game.onMouseClick(x - game.getCenter().getX(), y - game.getCenter().getY());
				}
			}

			totalDistanceDragged = 0;
			mousePressedInGame = false;
		}
	}

	/**
	 * @return x value of the position from the mouse
	 */
	public int getMouseX() {
		return mouseX;
	}

	/**
	 * @return y value of the position from the mouse
	 */
	public int getMouseY() {
		return mouseY;
	}
}
