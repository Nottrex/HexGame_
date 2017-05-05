package client.window;

public class Camera {
	public static final long ZOOM_TIME = 500;

	public float zoom, x, y;

	public float tx, ty;

	private float tzoom;
	private float targetZoom = tzoom;
	private long beginTime = 0, targetTime = 0;
	private float a, b, c, d;
	private boolean z = false;

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
		boolean b2 = (zoom!=tzoom) || (x!=tx) || (y!=ty) || z;

		if (z) {
			long time = System.currentTimeMillis()%10000000;
			if (time > targetTime) {
				tzoom = targetZoom;
				z = false;
			} else {
				tzoom = calculateFunction((time*1.0f-beginTime)/(targetTime-beginTime), a, b, c, d);
			}
		}

		zoom = tzoom;
		x = tx;
		y = ty;
		return b2;
	}

	public void zoomSmooth(float a2) {
		float v = 0;
		float t = tzoom;
		if (z) {
			v = calculateDerivate(((System.currentTimeMillis()%10000000)*1.0f-beginTime)/(targetTime-beginTime), a, b, c, d);
			t = targetZoom;
		}
		t *= a2;
		float currentZoom = tzoom;

		d = currentZoom;
		c = v;
		b = 3 * t - 2 * v - 3 * currentZoom;
		a = v + 2 * currentZoom - 2 * t;
		beginTime = System.currentTimeMillis()%10000000;
		targetTime = System.currentTimeMillis()%10000000 + ZOOM_TIME;
		targetZoom = t;

		z = true;
	}

	public void setZoom(float tzoom) {
		z = false;
		targetZoom = tzoom;
		this.tzoom = tzoom;
	}

	private float calculateFunction(float x, float a, float b, float c, float d) {
		return a*x*x*x + b*x*x + c*x + d;
	}

	private float calculateDerivate(float x, float a, float b, float c, float d) {
		return 3*a*x*x + 2*b*x + c;
	}
}
