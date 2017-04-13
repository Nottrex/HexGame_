package client.window.view.game.gameView.shader;

import client.window.FileHandler;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;

public abstract class ShaderProgramm {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;

	public ShaderProgramm(GL2 gl, String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(gl, vertexFile, gl.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(gl, fragmentFile, gl.GL_FRAGMENT_SHADER);

		programID = gl.glCreateProgram();
		gl.glAttachShader(programID, vertexShaderID);
		gl.glAttachShader(programID, fragmentShaderID);
		gl.glLinkProgram(programID);
		gl.glValidateProgram(programID);

		bindAttributes(gl);
		getUniformLocations(gl);
	}

	public void start(GL2 gl) {
		gl.glUseProgram(programID);
	}

	public void stop(GL2 gl) {
		gl.glUseProgram(0);
	}

	public void cleanUp(GL2 gl) {
		stop(gl);
		gl.glDetachShader(programID, vertexShaderID);
		gl.glDetachShader(programID, fragmentShaderID);
		gl.glDeleteShader(vertexShaderID);
		gl.glDeleteShader(fragmentShaderID);
		gl.glDeleteProgram(programID);
	}

	protected abstract void bindAttributes(GL2 gl);
	protected abstract void getUniformLocations(GL2 gl);

	protected void setUniform1f(GL2 gl, int location, float value) {
		gl.glUniform1f(location, value);
	}

	protected void setUniform2f(GL2 gl, int location, float v1, float v2) {
		gl.glUniform2f(location, v1, v2);
	}

	protected void setUniform1i(GL2 gl, int location, int i) {
		gl.glUniform1i(location, i);
	}

	protected void setUniformMat4(GL2 gl, int location, float[] v) {
		gl.glUniformMatrix4fv(location, 1, false, v, 0);
	}

	protected int getUniformLocation(GL2 gl, String name) {
		return gl.glGetUniformLocation(programID, name);
	}

	protected void bindAttribute(GL2 gl, int attribute, String variableName) {
		gl.glBindAttribLocation(programID, attribute, variableName);
	}

	private static int loadShader(GL2 gl, String file, int type) {
		String shaderSource = FileHandler.loadFile("shader/" + file + ".txt");
		int shaderID = gl.glCreateShader(type);
		gl.glShaderSource(shaderID, 1, new String[] {shaderSource}, null);
		gl.glCompileShader(shaderID);

		return shaderID;
	}

}
