package client.window;

public class Camera {
	public double zoom, x, y;

	public double tzoom, tx, ty;

	public Camera() {
		zoom = 1;
		x = 0;
		y = 0;
		tzoom = zoom;
		tx = x;
		ty = y;
	}

	/**
	 * Takes t-Values and put it to the inUse values
	 */
	public void update() {
		zoom = tzoom;
		x = tx;
		y = ty;
	}
}
