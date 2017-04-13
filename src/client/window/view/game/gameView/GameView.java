package client.window.view.game.gameView;

import client.Controller;
import client.window.Camera;
import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.view.game.gameView.shader.StaticShader;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import game.map.GameMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glClearColor(0f, 1f, 1f, 1f);

		//gl.glEnable(GL2.GL_ALPHA_TEST);
		//gl.glAlphaFunc(GL2.GL_GREATER,0.8f);

		shader = new StaticShader(gl);

		shader.start(gl);
		shader.setHexWidth(gl, 1f);
		shader.setHexHeight(gl, (float) GUIConstants.HEX_TILE_YY_RATIO);
		shader.setHexHeight2(gl, (float) GUIConstants.HEX_TILE_XY_RATIO);
		shader.setTexture(gl, 0);
		BufferedImage img = TextureHandler.getImagePng("field");
		shader.setTextureTotalBounds(gl, img.getWidth(), img.getHeight());
		shader.stop(gl);

		textureInit(gl);

		animator = new FPSAnimator(this, 60);
		animator.start();
	}

	private void textureInit(GL2 gl) {
		try {
			final ByteArrayOutputStream output = new ByteArrayOutputStream() {
				@Override
				public synchronized byte[] toByteArray() {
					return this.buf;
				}
			};
			ImageIO.write(TextureHandler.getImagePng("field"), "png", output);
			TextureData textured = TextureIO.newTextureData(this.getGLProfile(), new ByteArrayInputStream(output.toByteArray(), 0, output.size()), true, "png");
			Texture texture = TextureIO.newTexture(gl, textured);
			texture.enable(gl);
			gl.glActiveTexture(GL2.GL_TEXTURE0);
			texture.bind(gl);
			texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
			texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		} catch (Exception e) {e.printStackTrace();}
	}

	public void dispose(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		shader.cleanUp(gl);
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		controller.updateAnimationActions();
		updateCamera(gl, cam);

		shader.start(gl);

		GameMap map = controller.game.getMap();
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (!map.getFieldAt(x, y).isAccessible()) continue;

				Rectangle rec = TextureHandler.getSpriteSheetBounds("field_" + map.getFieldAt(x, y).toString().toLowerCase());
				shader.setLocation(gl, x, y);
				shader.setTextureSheetBounds(gl, rec.x, rec.y, rec.width, rec.height);

				gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 6);
			}
		}

		shader.stop(gl);

		gl.glFlush();
	}

	private float[] projectionMatrix;
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		float aspect = width*1.0f/height;
		float fov = 90;
		float near = 0.01f;
		float far = 1000f;

		projectionMatrix = FloatUtil.makePerspective(new float[16], 0, true, (float)Math.toRadians(fov), aspect, near, far);

		shader.start(gl);
		shader.setProjectionMatrix(gl, projectionMatrix);
		shader.stop(gl);

		textureInit(gl);
		gl.glViewport(x, y, width, height);
	}

	private float[] viewMatrix;
	private float[] cameraPosition;
	private void updateCamera(GL2 gl, Camera cam) {
		boolean b = cam.update();

		if (viewMatrix==null||b) {
			float[] target = {-cam.x, -cam.y, 0};
			cameraPosition = new float[] {-cam.x, -cam.y, 1/cam.zoom};
			float[] up = {0, 1, 0};

			viewMatrix = FloatUtil.makeLookAt(new float[16], 0, cameraPosition, 0, target, 0, up, 0, new float[16]);

			shader.start(gl);
			shader.setCamera(gl, viewMatrix);
			shader.stop(gl);
		}
	}

	public float[] screenPositionToWorldPosition(int x, int y) {
		float[] ray_nds = {(x*1.0f/getWidth())*2-1, ((y*1.0f/getHeight())*2-1), 1.0f};
		float[] ray_clip = {ray_nds[0], ray_nds[1], -1.0f, 1.0f};

		float[] ray_eye = FloatUtil.multMatrixVec(FloatUtil.invertMatrix(projectionMatrix, new float[16]), ray_clip, new float[4]);
		ray_eye[2] = -1.0f;
		ray_eye[3] = 0.0f;

		float[] ray_wor4 = FloatUtil.multMatrixVec(FloatUtil.invertMatrix(viewMatrix, new float[16]), ray_eye, new float[4]);
		float[] ray_wor = {ray_wor4[0], ray_wor4[1], ray_wor4[2]};
		ray_wor = VectorUtil.normalizeVec3(ray_wor);

		float distance = -cameraPosition[2]/ray_wor[2];
		float[] point = {distance*ray_wor[0]+cameraPosition[0], distance*ray_wor[1]-cameraPosition[1]};

		return point;
	}
}
