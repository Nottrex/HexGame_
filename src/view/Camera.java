package view;

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

	public void update() {
		zoom = tzoom;
		x = tx;
		y = ty;
	}
}
