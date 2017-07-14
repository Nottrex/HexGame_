package client.game.gameView.shader;

import com.jogamp.opengl.GL2;

public class HealthBarShader extends ShaderProgram {
	private static final String VERTEX_FILE = "healthBarVertexShader";
	private static final String FRAGMENT_FILE = "healthBarFragmentShader";
	private int xLocation, yLocation, widthLocation, heightLocation, cameraLocation, projectionLocation;
	private int timeLocation, healthLocation;

	public HealthBarShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getUniformLocations(GL2 gl) {
		xLocation = getUniformLocation(gl, "x");
		yLocation = getUniformLocation(gl, "y");
		widthLocation = getUniformLocation(gl, "width");
		heightLocation = getUniformLocation(gl, "height");
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");
		healthLocation = getUniformLocation(gl, "health");
		timeLocation = getUniformLocation(gl, "time");
	}

	public void setBounds(GL2 gl, float x, float y, float width, float height) {
		setUniform1f(gl, xLocation, x);
		setUniform1f(gl, yLocation, y);
		setUniform1f(gl, widthLocation, width);
		setUniform1f(gl, heightLocation, height);
	}

	public void setCamera(GL2 gl, float[] camera) {
		setUniformMat4(gl, cameraLocation, camera);
	}

	public void setProjectionMatrix(GL2 gl, float[] projectionMatrix) {
		setUniformMat4(gl, projectionLocation, projectionMatrix);
	}

	public void setTime(GL2 gl, float time) {
		setUniform1f(gl, timeLocation, time);
	}

	public void setHealth(GL2 gl, float health) {
		setUniform1f(gl, healthLocation, health);
	}

	@Override
	protected void bindAttributes(GL2 gl) {

	}
}
