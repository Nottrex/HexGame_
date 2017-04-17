package client.window.view.game.gameView.shader;

import com.jogamp.opengl.GL2;

public class SquareShader extends ShaderProgram {
	private static final String VERTEX_FILE = "squareVertexShader";
	private static final String FRAGMENT_FILE = "squareFragmentShader";

	public SquareShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	private int texLocation, xLocation, yLocation, widthLocation, heightLocation, cameraLocation, projectionLocation;
	private int texXLocation, texYLocation, texTWLocation, texTHLocation, texWLocation, texHLocation, timeLocation;
	@Override
	protected void getUniformLocations(GL2 gl) {
		texLocation = getUniformLocation(gl, "tex");
		xLocation = getUniformLocation(gl, "x");
		yLocation = getUniformLocation(gl, "y");
		widthLocation = getUniformLocation(gl, "width");
		heightLocation = getUniformLocation(gl, "height");
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");

		texXLocation = getUniformLocation(gl, "texX");
		texYLocation = getUniformLocation(gl, "texY");
		texTWLocation = getUniformLocation(gl, "texTW");
		texTHLocation = getUniformLocation(gl, "texTH");
		texHLocation = getUniformLocation(gl, "texH");
		texWLocation = getUniformLocation(gl, "texW");

		timeLocation = getUniformLocation(gl, "time");
	}

	public void setTexture(GL2 gl, int tex) {
		setUniform1i(gl, texLocation, tex);
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

	public void setTextureSheetBounds(GL2 gl, int x, int y, int width, int height) {
		setUniform1i(gl, texXLocation, x);
		setUniform1i(gl, texYLocation, y);
		setUniform1i(gl, texWLocation, width);
		setUniform1i(gl, texHLocation, height);
	}

	public void setTextureTotalBounds(GL2 gl, int texTW, int texTH) {
		setUniform1i(gl, texTWLocation, texTW);
		setUniform1i(gl, texTHLocation, texTH);
	}

	public void setTime(GL2 gl, float time) {
		setUniform1f(gl, timeLocation, time);
	}

	@Override
	protected void bindAttributes(GL2 gl) {

	}
}
