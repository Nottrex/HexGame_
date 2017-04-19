package client.window.view.game.gameView.shader;

import client.window.FileHandler;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;

public abstract class ShaderProgram {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;

	public ShaderProgram(GL2 gl, String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(gl, vertexFile, gl.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(gl, fragmentFile, gl.GL_FRAGMENT_SHADER);

		programID = gl.glCreateProgram();
		gl.glAttachShader(programID, vertexShaderID);
		gl.glAttachShader(programID, fragmentShaderID);
		gl.glLinkProgram(programID);
		gl.glValidateProgram(programID);

		int[] error = new int[]{-1};

		gl.glGetShaderiv(vertexShaderID, GL2.GL_COMPILE_STATUS, error, 0);
		if (error[0] != GL2.GL_TRUE) {
			int[] len = new int[1];
			gl.glGetShaderiv(vertexShaderID, GL2.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] == 0) {
				System.err.println("No error stated2");
				System.exit(-1);
			}
			byte[] errormessage = new byte[len[0]];
			gl.glGetShaderInfoLog(vertexShaderID, len[0], len, 0, errormessage, 0);

			System.err.println(this.getClass().toString() + ": vertexShader: " + new String(errormessage, 0, len[0] + 1));
			System.exit(-1);
		}

		gl.glGetShaderiv(fragmentShaderID, GL2.GL_COMPILE_STATUS, error, 0);

		if (error[0] != GL2.GL_TRUE) {
			int[] len = new int[1];
			gl.glGetShaderiv(fragmentShaderID, GL2.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] == 0) {
				System.err.println("No error stated3");
				System.exit(-1);
			}
			byte[] errormessage = new byte[len[0]];
			gl.glGetShaderInfoLog(fragmentShaderID, len[0], len, 0, errormessage, 0);

			System.err.println(this.getClass().toString() + ": fragmentShader: " + new String(errormessage, 0, len[0] + 1));
			System.exit(-1);
		}


		gl.glGetProgramiv(programID, GL2.GL_LINK_STATUS, error, 0);
		if (error[0] != GL2.GL_TRUE) {
			int[] len = new int[1];
			gl.glGetProgramiv(programID, GL2.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] == 0) {
				System.err.println("No error stated1");
				System.exit(-1);
			}
			byte[] errormessage = new byte[len[0]];
			gl.glGetProgramInfoLog(programID, len[0], len, 0, errormessage, 0);
			System.err.println(new String(errormessage, 0, len[0]));
			System.exit(-1);
		}

		bindAttributes(gl);
		getUniformLocations(gl);
		//gl.glLinkProgram(programID);
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

	protected void setUniform4f(GL2 gl, int location, float v1, float v2, float v3, float v4) {
		gl.glUniform4f(location, v1, v2, v3, v4);
	}

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

	protected int getAttributeLocation(GL2 gl, String variableName) {
		return gl.glGetAttribLocation(programID, variableName);
	}

	private static int loadShader(GL2 gl, String file, int type) {
		String shaderSource = FileHandler.loadFile("shader/" + file + ".txt");
		int shaderID = gl.glCreateShader(type);
		gl.glShaderSource(shaderID, 1, new String[] {shaderSource}, null);
		gl.glCompileShader(shaderID);

		return shaderID;
	}

}
