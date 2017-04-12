package client.window.view.game.gameView.shader;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;

public class StaticShader extends ShaderProgramm{
	private static final String VERTEX_FILE = "vertexShader";
	private static final String FRAGMENT_FILE = "fragmentShader";

	public StaticShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	private int texLocation, hexWidthLocation, hexHeightLocation, hexHeight2Location, locationLocation, cameraLocation, projectionLocation;
	@Override
	protected void getUniformLocations(GL2 gl) {
		texLocation = getUniformLocation(gl, "tex");
		hexWidthLocation = getUniformLocation(gl, "hexWidth");
		hexHeightLocation = getUniformLocation(gl, "hexHeight");
		locationLocation = getUniformLocation(gl, "location");
		hexHeight2Location = getUniformLocation(gl, "hexHeight2");
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");
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

	@Override
	protected void bindAttributes(GL2 gl) {

	}
}
