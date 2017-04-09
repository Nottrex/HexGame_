package client.window.view.game.gameView;

import client.Controller;
import client.window.Camera;
import client.window.GUIConstants;
import client.window.view.game.gameView.shader.StaticShader;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import game.map.GameMap;

public class GameView extends GLJPanel implements GLEventListener {

	private Controller controller;
	private Camera cam;
	private StaticShader shader;
	public GameView(GLCapabilities capabilities, Controller controller, Camera cam) {
		super(capabilities);
		this.controller = controller;
		this.cam = cam;
		setFocusable(true);
		this.addGLEventListener(this);
	}

	public FPSAnimator animator;
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		drawable.setGL((new DebugGL2(gl)));

		gl.setSwapInterval(1);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glClearColor(0f, 1f, 1f, 1f);

		gl.glEnable(GL2.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL2.GL_GREATER,0.8f);

		shader = new StaticShader(gl);

		shader.start(gl);
		shader.setHexWidth(gl, 1f);
		shader.setHexHeight(gl, (float) GUIConstants.HEX_TILE_YY_RATIO);
		shader.setTexture(gl, 0);
		shader.stop(gl);

		//TextureHandler.loadImagePng("test", "test");
		try {
			TextureData textured = TextureIO.newTextureData(this.getGLProfile(), ClassLoader.getSystemResourceAsStream("res/textures/test.png"), true, "png");
			Texture texture = TextureIO.newTexture(gl, textured);
			texture.enable(gl);
			gl.glActiveTexture(GL2.GL_TEXTURE0);
			texture.bind(gl);
		} catch (Exception e) {e.printStackTrace();}

		animator = new FPSAnimator(this, 60);
		animator.start();
	}

	public void dispose(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		shader.cleanUp(gl);
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		controller.updateAnimationActions();
		cam.update();
		updateCamera(gl, cam);

		shader.start(gl);

		GameMap map = controller.game.getMap();
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				shader.setLocation(gl, x, y);
				gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
			}
		}

		shader.stop(gl);

		gl.glFlush();
	}

	private void updateCamera(GL2 gl, Camera cam) {
		Matrix4 viewMatrix = new Matrix4();
		viewMatrix.loadIdentity();
		viewMatrix.translate(-cam.x, cam.y, -1);
		viewMatrix.scale(cam.zoom, cam.zoom, cam.zoom);

		shader.start(gl);
		shader.setCamera(gl, viewMatrix);
		shader.stop(gl);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		float aspect = width*1.0f/height;
		float fov = 90;
		float near = 0.1f;
		float far = 1000f;

		Matrix4 m = new Matrix4();
		m.makePerspective((float)Math.toRadians(fov), aspect, near, far);

		shader.start(gl);
		shader.setProjectionMatrix(gl, m);
		shader.stop(gl);

		gl.glViewport(x, y, width, height);
	}
}
