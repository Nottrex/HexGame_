package client.window.view.game.gameView.shader;

import com.jogamp.opengl.GL2;

public class FieldmarkerShader extends ShaderProgram {
	private static final String VERTEX_FILE = "fieldmarkerVertexShader";
	private static final String FRAGMENT_FILE = "fieldmarkerFragmentShader";

	public FieldmarkerShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	private int texLocation, hexWidthLocation, hexHeightLocation, hexHeight2Location, locationLocation, cameraLocation, projectionLocation;
	private int texXLocation, texYLocation, texTWLocation, texTHLocation, texWLocation, texHLocation;
	@Override
	protected void getUniformLocations(GL2 gl) {
		texLocation = getUniformLocation(gl, "tex");
		hexWidthLocation = getUniformLocation(gl, "hexWidth");
		hexHeightLocation = getUniformLocation(gl, "hexHeight");
		locationLocation = getUniformLocation(gl, "location");
		hexHeight2Location = getUniformLocation(gl, "hexHeight2");
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");

		texXLocation = getUniformLocation(gl, "texX");
		texYLocation = getUniformLocation(gl, "texY");
		texTWLocation = getUniformLocation(gl, "texTW");
		texTHLocation = getUniformLocation(gl, "texTH");
		texHLocation = getUniformLocation(gl, "texH");
		texWLocation = getUniformLocation(gl, "texW");
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

	public void setLocation(GL2 gl, float locationX, float locationY) {
		setUniform2f(gl, locationLocation, locationX, locationY);
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

	@Override
	protected void bindAttributes(GL2 gl) {

	}
}
