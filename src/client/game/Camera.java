package client.game;

public class Camera {
	public static final long ZOOM_TIME = 200;

	public float zoom, x, y;

	private float tx, ty;
	private boolean z2 = false;
	private float targetX, targetY;
	private long beginTime2 = 0, targetTime2 = 0;
	private float a2, b2, c2, d2;
	private float a3, b3, c3, d3;

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
		boolean b5 = (zoom!=tzoom) || (x!=tx) || (y!=ty) || z || z2;
		long time = System.currentTimeMillis()%10000000;

		if (z) {
			if (time > targetTime) {
				tzoom = targetZoom;
				z = false;
			} else {
				tzoom = calculateFunction((time*1.0f-beginTime)/(targetTime-beginTime), a, b, c, d);
			}
		}

		if (z2) {
			if (time > targetTime2) {
				tx = targetX;
				ty = targetY;
				z2 = false;
			} else {
				tx = calculateFunction((time*1.0f-beginTime2)/(targetTime2-beginTime2), a2, b2, c2, d2);
				ty = calculateFunction((time*1.0f-beginTime2)/(targetTime2-beginTime2), a3, b3, c3, d3);
			}
		}

		zoom = tzoom;
		x = tx;
		y = ty;
		return b5;
	}

	public void zoomSmooth(float a2) {
		zoomSmooth(a2, ZOOM_TIME);
	}

	private void zoomSmooth(float a2, long time) {
		float v = 0;
		float t = tzoom;
		if (z) {
			v = calculateDerivative(((System.currentTimeMillis()%10000000)*1.0f-beginTime)/(targetTime-beginTime), a, b, c, d);
			t = targetZoom;
		}
		t *= a2;
		float currentZoom = tzoom;

		d = currentZoom;
		c = v;
		b = 3 * t - 2 * v - 3 * currentZoom;
		a = v + 2 * currentZoom - 2 * t;
		beginTime = System.currentTimeMillis()%10000000;
		targetTime = System.currentTimeMillis()%10000000 + time;
		targetZoom = t;

		z = true;
	}

	public void setZoomSmooth(float tzoom, long time) {
		zoomSmooth(tzoom/this.tzoom, time);
		z = true;
	}

	public void setZoom(float tzoom) {
		z = false;
		this.tzoom = tzoom;
	}


	public void setPosition(float x, float y) {
		z2 = false;
		this.tx = x;
		this.ty = y;
	}

	public void setPositionSmooth(float x, float y, long time) {
		float v2 = 0, v3 = 0;
		float t2 = x, t3 = y;
		if (z2) {
			v2 = calculateDerivative(((System.currentTimeMillis()%10000000)*1.0f-beginTime2)/(targetTime2-beginTime2), a2, b2, c2, d2);
			v3 = calculateDerivative(((System.currentTimeMillis()%10000000)*1.0f-beginTime2)/(targetTime2-beginTime2), a3, b3, c3, d3);
		}
		float currentX = tx, currentY = ty;

		d2 = currentX;
		c2 = v2;
		b2 = 3 * t2 - 2 * v2 - 3 * currentX;
		a2 = v2 + 2 * currentX - 2 * t2;

		d3 = currentY;
		c3 = v3;
		b3 = 3 * t3 - 2 * v3 - 3 * currentY;
		a3 = v3 + 2 * currentY - 2 * t3;

		beginTime2 = System.currentTimeMillis()%10000000;
		targetTime2 = System.currentTimeMillis()%10000000 + time;
		targetX = x;
		targetY = y;

		z2 = true;
	}

	public float getZoom() {
		return tzoom;
	}

	public float getX() {
		return tx;
	}

	public float getY() {
		return ty;
	}

	private float calculateFunction(float x, float a, float b, float c, float d) {
		return a*x*x*x + b*x*x + c*x + d;
	}

	private float calculateDerivative(float x, float a, float b, float c, float d) {
		return 3*a*x*x + 2*b*x + c;
	}
}
