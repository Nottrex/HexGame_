package client.window;

public class Camera {
	public float zoom, x, y;

	public float tzoom, tx, ty;

	public Camera() {
		zoom = 3;
		x = 0;
		y = 0;
		tzoom = zoom;
		tx = x;
		ty = y;
	}

	/**
	 * Takes t-Values and put it to the inUse values
	 */
	public boolean update() {
		boolean b = (zoom!=tzoom) || (x!=tx) || (y!=ty);

		zoom = tzoom;
		x = tx;
		y = ty;
		return b;
	}
}
