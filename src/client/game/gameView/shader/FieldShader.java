package client.game.gameView.shader;

import com.jogamp.opengl.GL2;

public class FieldShader extends ShaderProgram {
	private static final String VERTEX_FILE = "fieldVertexShader";
	private static final String FRAGMENT_FILE = "fieldFragmentShader";

	public FieldShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	private int texLocation, cameraLocation, projectionLocation;
	private int timeLocation, camZLocation, fogBoundsLocation;
	@Override
	protected void getUniformLocations(GL2 gl) {
		texLocation = getUniformLocation(gl, "tex");
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");

		timeLocation = getUniformLocation(gl, "time");
		camZLocation = getUniformLocation(gl, "cam_z");

		fogBoundsLocation = getUniformLocation(gl, "fogBounds");
	}

	public void setFogBounds(GL2 gl, float x, float y, float width, float height) {
		setUniform4f(gl, fogBoundsLocation, x, y,width, height);
	}

	public void setTexture(GL2 gl, int tex) {
		setUniform1i(gl, texLocation, tex);
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

	public void setCamZ(GL2 gl, float cam_z) {
		setUniform1f(gl, camZLocation, cam_z);
	}

	private int locationLocation, texLocationLocation, fieldDataLocation;
	@Override
	protected void bindAttributes(GL2 gl) {
		locationLocation = getAttributeLocation(gl, "location");
		texLocationLocation = getAttributeLocation(gl, "texLocation");
		fieldDataLocation = getAttributeLocation(gl, "fieldData");
	}

	public int getLocationLocation() {
		return locationLocation;
	}

	public int getTexLocationLocation() {
		return texLocationLocation;
	}

	public  int getFieldDataLocation() {
		return fieldDataLocation;
	}
}
