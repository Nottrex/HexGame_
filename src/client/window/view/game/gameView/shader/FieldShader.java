package client.window.view.game.gameView.shader;

import com.jogamp.opengl.GL2;

public class FieldShader extends ShaderProgram {
	private static final String VERTEX_FILE = "fieldVertexShader";
	private static final String FRAGMENT_FILE = "fieldFragmentShader";

	public FieldShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	private int texLocation, hexWidthLocation, hexHeightLocation, hexHeight2Location, cameraLocation, projectionLocation;
	private int texTWLocation, texTHLocation;
	private int timeLocation;
	@Override
	protected void getUniformLocations(GL2 gl) {
		texLocation = getUniformLocation(gl, "tex");
		hexWidthLocation = getUniformLocation(gl, "hexWidth");
		hexHeightLocation = getUniformLocation(gl, "hexHeight");
		hexHeight2Location = getUniformLocation(gl, "hexHeight2");
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");

		timeLocation = getUniformLocation(gl, "time");
		texTWLocation = getUniformLocation(gl, "texTW");
		texTHLocation = getUniformLocation(gl, "texTH");
	}

	public void setTexture(GL2 gl, int tex) {
		setUniform1i(gl, texLocation, tex);
	}

	public void setHexWidth(GL2 gl, float width) {
		setUniform1f(gl, hexWidthLocation, width);
	}

	public void setHexHeight2(GL2 gl, float height2) {
		setUniform1f(gl, hexHeight2Location, height2);
	}

	public void setHexHeight(GL2 gl, float height) {
		setUniform1f(gl, hexHeightLocation, height);
	}

	public void setCamera(GL2 gl, float[] camera) {
		setUniformMat4(gl, cameraLocation, camera);
	}

	public void setProjectionMatrix(GL2 gl, float[] projectionMatrix) {
		setUniformMat4(gl, projectionLocation, projectionMatrix);
	}

	public void setTextureTotalBounds(GL2 gl, int texTW, int texTH) {
		setUniform1i(gl, texTWLocation, texTW);
		setUniform1i(gl, texTHLocation, texTH);
	}

	public void setTime(GL2 gl, float time) {
		setUniform1f(gl, timeLocation, time);
	}

	private int locationLocation, texLocationLocation;
	@Override
	protected void bindAttributes(GL2 gl) {
		locationLocation = getAttributeLocation(gl, "location");
		texLocationLocation = getAttributeLocation(gl, "texLocation");
	}

	public int getLocationLocation() {
		return locationLocation;
	}

	public int getTexLocationLocation() {
		return texLocationLocation;
	}
}
