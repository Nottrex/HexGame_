package client.window.view.game.gameView;

import client.AnimationAction;
import client.Controller;
import client.animationActions.AnimationActionUnitMove;
import client.window.Camera;
import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.view.game.gameView.shader.FieldShader;
import client.window.view.game.gameView.shader.FieldmarkerShader;
import client.window.view.game.gameView.shader.SquareShader;
import client.window.view.game.gameView.shader.UnitShader;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import game.Location;
import game.Unit;
import game.enums.Direction;
import game.enums.Field;
import game.enums.UnitType;
import game.map.GameMap;
import game.util.ActionUtil;
import game.util.PossibleActions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

public class GameView extends GLJPanel implements GLEventListener {

	private Controller controller;
	private Camera cam;

	private IntBuffer buffers = IntBuffer.allocate(2);
	private IntBuffer vertexArray = IntBuffer.allocate(1);
	private int length;
	private Texture fieldTexture;
	private FieldShader fieldShader;

	private Texture fieldmarkerTexture;
	private FieldmarkerShader fieldmarkerShader;

	private Texture arrowTexture;
	private SquareShader squareShader;

	private Texture unitTexture;
	private UnitShader unitShader;

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

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

		fieldShader = new FieldShader(gl);

		fieldShader.start(gl);
		fieldShader.setHexWidth(gl, 1f);
		fieldShader.setHexHeight(gl, (float) GUIConstants.HEX_TILE_YY_RATIO);
		fieldShader.setHexHeight2(gl, (float) GUIConstants.HEX_TILE_XY_RATIO);
		fieldShader.setTexture(gl, 0);
		BufferedImage img = TextureHandler.getImagePng("field");
		fieldShader.setTextureTotalBounds(gl, img.getWidth(), img.getHeight());
		fieldShader.stop(gl);

		GameMap map = controller.game.getMap();
		length = 0;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (map.getFieldAt(x, y).isAccessible()) {
					length++;
				}
			}
		}

		float[] locations = new float[length*2*12];
		float[] texLocations = new float[length*4*12];

		int a = 0;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (map.getFieldAt(x, y).isAccessible()) {
					Field f = map.getFieldAt(x, y);
					Rectangle rec = TextureHandler.getSpriteSheetBounds("field_" + f.toString().toLowerCase());
					for(int i = 0; i < 12; i++) {
						locations[12*2*a+i*2] = x;
						locations[12*2*a+1+i*2] = y;

						texLocations[12*4*a+i*4] = rec.x;
						texLocations[12*4*a+1+i*4] = rec.y;
						texLocations[12*4*a+2+i*4] = rec.width;
						texLocations[12*4*a+3+i*4] = rec.height;

					}
					a++;
				}
			}
		}

		FloatBuffer locationBuffer = FloatBuffer.wrap(locations);
		FloatBuffer texLocationBuffer = FloatBuffer.wrap(texLocations);

		gl.glGenBuffers(2, buffers);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, 4*length*12 * 2, locationBuffer, GL2.GL_STATIC_DRAW);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(1));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, 4*length*12 * 4, texLocationBuffer, GL2.GL_STATIC_DRAW);

		gl.glGenVertexArrays(1, vertexArray);
		gl.glBindVertexArray(vertexArray.get(0));

		gl.glEnableVertexAttribArray(fieldShader.getLocationLocation());
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glVertexAttribPointer(fieldShader.getLocationLocation(), 2, GL.GL_FLOAT, false, 0, 0);

		gl.glEnableVertexAttribArray(fieldShader.getTexLocationLocation());
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(1));
		gl.glVertexAttribPointer(fieldShader.getTexLocationLocation(), 4, GL.GL_FLOAT, false, 0, 0);

		fieldmarkerShader = new FieldmarkerShader(gl);

		fieldmarkerShader.start(gl);
		fieldmarkerShader.setHexWidth(gl, 1f);
		fieldmarkerShader.setHexHeight(gl, (float) GUIConstants.HEX_TILE_YY_RATIO);
		fieldmarkerShader.setHexHeight2(gl, (float) GUIConstants.HEX_TILE_XY_RATIO);
		fieldmarkerShader.setTexture(gl, 0);
		img = TextureHandler.getImagePng("fieldmarker");
		fieldmarkerShader.setTextureTotalBounds(gl, img.getWidth(), img.getHeight());
		fieldmarkerShader.stop(gl);

		squareShader = new SquareShader(gl);

		squareShader.start(gl);
		squareShader.setTexture(gl, 0);
		img = TextureHandler.getImagePng("arrow");
		squareShader.setTextureTotalBounds(gl, img.getWidth(), img.getHeight());
		squareShader.stop(gl);

		unitShader = new UnitShader(gl);


		unitShader.start(gl);
		unitShader.setTexture(gl, 0);
		img = TextureHandler.getImagePng("unit");
		unitShader.setTextureTotalBounds(gl, img.getWidth(), img.getHeight());
		unitShader.stop(gl);

		textureInit(gl);

		animator = new FPSAnimator(this, 60);
		animator.setUpdateFPSFrames(60, null);
		animator.start();
	}

	private void textureInit(GL2 gl) {
		gl.glActiveTexture(GL2.GL_TEXTURE0);

		fieldmarkerTexture = TextureIO.newTexture(gl, toTexture(this.getGLProfile(), TextureHandler.getImagePng("fieldmarker")));
		fieldmarkerTexture.bind(gl);
		fieldmarkerTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		fieldmarkerTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

		fieldTexture = TextureIO.newTexture(gl, toTexture(this.getGLProfile(), TextureHandler.getImagePng("field")));
		fieldTexture.bind(gl);
		fieldTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		fieldTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

		arrowTexture = TextureIO.newTexture(gl, toTexture(this.getGLProfile(), TextureHandler.getImagePng("arrow")));
		arrowTexture.bind(gl);
		arrowTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		arrowTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

		unitTexture = TextureIO.newTexture(gl, toTexture(this.getGLProfile(), TextureHandler.getImagePng("unit")));
		unitTexture.bind(gl);
		unitTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		unitTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
	}

	public void dispose(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		fieldShader.cleanUp(gl);
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		controller.updateAnimationActions();
		AnimationAction currentAnimation = controller.getAnimationAction();
		updateCamera(gl, cam);

		fieldTexture.bind(gl);
		fieldShader.start(gl);
		gl.glBindVertexArray(vertexArray.get(0));
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, length*12);
		gl.glBindVertexArray(0);
		fieldShader.stop(gl);


		GameMap map = controller.game.getMap();

		fieldmarkerTexture.bind(gl);
		fieldmarkerShader.start(gl);

		Location selectedField = controller.selectedField;
		if (selectedField != null) {
			fieldmarkerShader.setLocation(gl, selectedField.x, selectedField.y);
			Rectangle rec = TextureHandler.getSpriteSheetBounds("fieldmarker_normalYellow");
			fieldmarkerShader.setTextureSheetBounds(gl, rec.x, rec.y, rec.width, rec.height);
			gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 6);

			Optional<Unit> u = map.getUnitAt(selectedField);

			if (u.isPresent()) {
				if (controller.pa == null) {
					controller.pa = ActionUtil.getPossibleActions(controller.game, u.get());
				}
				PossibleActions pa = controller.pa;

				for (Location target: pa.canMoveTo()) {
					fieldmarkerShader.setLocation(gl, target.x, target.y);
					rec = TextureHandler.getSpriteSheetBounds("fieldmarker_normalBlue");
					fieldmarkerShader.setTextureSheetBounds(gl, rec.x, rec.y, rec.width, rec.height);
					gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 6);
				}

				for (Location target: pa.canAttack()) {
					fieldmarkerShader.setLocation(gl, target.x, target.y);
					rec = TextureHandler.getSpriteSheetBounds("fieldmarker_normalRed");
					fieldmarkerShader.setTextureSheetBounds(gl, rec.x, rec.y, rec.width, rec.height);
					gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 6);
				}
			}
		}
		Location mouseLocation = controller.hoverField;
		if (mouseLocation != null) {
			fieldmarkerShader.setLocation(gl, mouseLocation.x, mouseLocation.y);
			Rectangle rec = TextureHandler.getSpriteSheetBounds("fieldmarker_select");
			fieldmarkerShader.setTextureSheetBounds(gl, rec.x, rec.y, rec.width, rec.height);
			gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 6);
		}

		fieldmarkerShader.stop(gl);

		if (selectedField != null && mouseLocation != null) {
			Optional<Unit> u = map.getUnitAt(selectedField);

			if (u.isPresent()) {
				PossibleActions pa = controller.pa;

				if (pa != null) {
					arrowTexture.bind(gl);
					squareShader.start(gl);

					if (pa.canAttack().contains(mouseLocation)) {
						java.util.List<Direction> movements = pa.moveToToAttack(mouseLocation);

						Location a = new Location(selectedField.x, selectedField.y);

						for (Direction d : movements) {
							drawMovementArrow(gl, squareShader, a, d);
							a = d.applyMovement(a);
						}
					}

					if (pa.canMoveTo().contains(mouseLocation)) {
						java.util.List<Direction> movements = pa.moveTo(mouseLocation);

						Location a = new Location(selectedField.x, selectedField.y);

						for (Direction d : movements) {
							drawMovementArrow(gl, squareShader, a, d);
							a = d.applyMovement(a);
						}
					}

					squareShader.stop(gl);
				}
			}
		}

		unitTexture.bind(gl);
		unitShader.start(gl);
		for (Unit unit: map.getUnits()) {
			UnitType ut = unit.getType();
			double w = ut.getSize();
			double h = w*GUIConstants.UNIT_XY_RATIO;

			double py = -((unit.getY())*(GUIConstants.HEX_TILE_YY_RATIO)*GUIConstants.HEX_TILE_XY_RATIO - (GUIConstants.HEX_TILE_XY_RATIO-h)/2);
			double px = unit.getX() - unit.getY()/2.0 + (1-w)/2;

			if (currentAnimation != null && currentAnimation instanceof AnimationActionUnitMove) {
				AnimationActionUnitMove animation = (AnimationActionUnitMove) currentAnimation;

				if (animation.getUnit() == unit) {
					py = -((unit.getY()+animation.getCurrentDirection().getYMovement()*animation.interpolation())*(GUIConstants.HEX_TILE_YY_RATIO)*GUIConstants.HEX_TILE_XY_RATIO - (GUIConstants.HEX_TILE_XY_RATIO-h)/2);
					px = (unit.getX()+animation.getCurrentDirection().getXMovement()*animation.interpolation()) - (unit.getY()+animation.getCurrentDirection().getYMovement()*animation.interpolation())/2.0 + (1-w)/2;
				}
			}

			unitShader.setBounds(gl, (float) px, (float) py, (float) w, (float) h);
			Rectangle rec = TextureHandler.getSpriteSheetBounds("unit_" + ut.toString().toLowerCase() + "_" + unit.getPlayer().toString().toLowerCase());
			unitShader.setTextureSheetBounds(gl, rec.x, rec.y, rec.width, rec.height);

			gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
		}

		unitShader.stop(gl);

		gl.glFlush();
	}

	private static void drawMovementArrow(GL2 gl, SquareShader squareShader, Location location, Direction d) {
		double wx = 1;
		double wy = GUIConstants.HEX_TILE_XY_RATIO;
		double centerY1 = -((location.y)*(GUIConstants.HEX_TILE_YY_RATIO)*wy - wy/2);
		double centerX1 = (location.x)*wx - (location.y)*wy/(2* GUIConstants.HEX_TILE_XY_RATIO) + wx/2;

		double centerY2 = -((location.y+d.getYMovement())*(GUIConstants.HEX_TILE_YY_RATIO)*wy - wy/2);
		double centerX2 = (location.x+d.getXMovement())*wx - (location.y+d.getYMovement())*wy/(2* GUIConstants.HEX_TILE_XY_RATIO) + wx/2;

		float[] position = null;
		Rectangle spriteSheetPosition = null;
		switch (d) {
			case RIGHT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_right");
				position = new float[] {(float) (centerX1+wx*GUIConstants.ARROW_SIZE/2), (float) (centerY2 - wx*GUIConstants.ARROW_SIZE/2), (float) (wx*GUIConstants.ARROW_SIZE), (float) (GUIConstants.ARROW_SIZE*wx)};
				break;
			case LEFT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_left");
				position = new float[]{(float) (centerX2+wx*GUIConstants.ARROW_SIZE/2), (float) (centerY2 - wx*GUIConstants.ARROW_SIZE/2), (float) (wx*GUIConstants.ARROW_SIZE), (float) (GUIConstants.ARROW_SIZE*wx)};
				break;
			case UP_RIGHT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_up_right");
				position = new float[] {(float) (centerX1+((centerX2-centerX1)-wx*GUIConstants.ARROW_SIZE)/2), (float) (centerY1 + ((centerY2-centerY1) - wx*GUIConstants.ARROW_SIZE)/2), (float) (wx*GUIConstants.ARROW_SIZE), (float) (wx*GUIConstants.ARROW_SIZE)};
				break;
			case UP_LEFT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_up_left");
				position = new float[] {(float) (centerX1+((centerX2-centerX1)-wx*GUIConstants.ARROW_SIZE)/2), (float) (centerY1 + ((centerY2-centerY1) - wx*GUIConstants.ARROW_SIZE)/2), (float) (wx*GUIConstants.ARROW_SIZE), (float) (wx*GUIConstants.ARROW_SIZE)};
				break;
			case DOWN_LEFT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_down_left");
				position = new float[] {(float) (centerX1+((centerX2-centerX1)-wx*GUIConstants.ARROW_SIZE)/2), (float) (centerY1 + ((centerY2-centerY1) - wx*GUIConstants.ARROW_SIZE)/2), (float) (wx*GUIConstants.ARROW_SIZE), (float) (wx*GUIConstants.ARROW_SIZE)};
				break;
			case DOWN_RIGHT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_down_right");
				position = new float[] {(float) (centerX1+((centerX2-centerX1)-wx*GUIConstants.ARROW_SIZE)/2), (float) (centerY1 + ((centerY2-centerY1) - wx*GUIConstants.ARROW_SIZE)/2), (float) (wx*GUIConstants.ARROW_SIZE), (float) (wx*GUIConstants.ARROW_SIZE)};
				break;
			default:
				break;
		}

		squareShader.setTextureSheetBounds(gl, spriteSheetPosition.x, spriteSheetPosition.y, spriteSheetPosition.width, spriteSheetPosition.height);
		squareShader.setBounds(gl, position[0], position[1], position[2], position[3]);
		gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
	}

	private float[] projectionMatrix;
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		float aspect = width*1.0f/height;
		float fov = 90;
		float near = 0.01f;
		float far = 1000f;

		projectionMatrix = FloatUtil.makePerspective(new float[16], 0, true, (float)Math.toRadians(fov), aspect, near, far);

		fieldShader.start(gl);
		fieldShader.setProjectionMatrix(gl, projectionMatrix);
		fieldShader.stop(gl);

		fieldmarkerShader.start(gl);
		fieldmarkerShader.setProjectionMatrix(gl, projectionMatrix);
		fieldmarkerShader.stop(gl);

		squareShader.start(gl);
		squareShader.setProjectionMatrix(gl, projectionMatrix);
		squareShader.stop(gl);

		unitShader.start(gl);
		unitShader.setProjectionMatrix(gl, projectionMatrix);
		unitShader.stop(gl);

		textureInit(gl);
		gl.glViewport(x, y, width, height);
	}

	private float[] viewMatrix = null;
	private float[] cameraPosition = null;
	private void updateCamera(GL2 gl, Camera cam) {
		boolean b = cam.update();
		if (cam.zoom == Double.POSITIVE_INFINITY || cam.zoom == Double.NEGATIVE_INFINITY || cam.zoom == Double.NaN) {
			cam.tzoom = 1;
			b = cam.update();
		}

		if (viewMatrix==null || b) {
			float[] target = {-cam.x, -cam.y, 0};
			cameraPosition = new float[] {-cam.x, -cam.y, 1/cam.zoom};
			float[] up = {0, 1, 0};

			viewMatrix = FloatUtil.makeLookAt(new float[16], 0, cameraPosition, 0, target, 0, up, 0, new float[16]);

			fieldShader.start(gl);
			fieldShader.setCamera(gl, viewMatrix);
			fieldShader.stop(gl);

			fieldmarkerShader.start(gl);
			fieldmarkerShader.setCamera(gl, viewMatrix);
			fieldmarkerShader.stop(gl);

			squareShader.start(gl);
			squareShader.setCamera(gl, viewMatrix);
			squareShader.stop(gl);

			unitShader.start(gl);
			unitShader.setCamera(gl, viewMatrix);
			unitShader.stop(gl);
		}
	}

	public float[] screenPositionToWorldPosition(int x, int y) {
		if (projectionMatrix == null || viewMatrix == null || cam.zoom == Double.POSITIVE_INFINITY || cam.zoom == Double.NEGATIVE_INFINITY || cam.zoom == Double.NaN) return new float[] {-1, -1};

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

	public float[] hexPositionToWorldPosition(Location hexField) {
		int x = hexField.x;
		int y = hexField.y;

		float fx = x * 1 - y  / 2;
		float fy = (float)(-y * GUIConstants.HEX_TILE_YY_RATIO + GUIConstants.HEX_TILE_XY_RATIO);

		return new float[]{fx, fy};
	}

	public Location getHexFieldPosition(float px, float py) {
		py += GUIConstants.HEX_TILE_XY_RATIO;
		double dy = py / (GUIConstants.HEX_TILE_YY_RATIO*GUIConstants.HEX_TILE_XY_RATIO);

		int	y = (int) Math.floor(dy);
		int	x = (int) Math.floor(px + y/2.0);
		if (((dy%1)+1)%1 <= (1- GUIConstants.HEX_TILE_YY_RATIO) / (GUIConstants.HEX_TILE_YY_RATIO)) {
			double vx = (((px + y/2.0) % 1)+1)%1;
			double vy = ((((dy%1)+1)%1) / ((1- GUIConstants.HEX_TILE_YY_RATIO) / (GUIConstants.HEX_TILE_YY_RATIO)))/2;

			if (vx < 0.5) {
				if (vx + vy  < 0.5) {
					x--;
					y--;
				}
			} else {
				vx = 1 - vx;

				if (vx + vy  < 0.5) {
					y--;
				}
			}
		}

		return new Location(x, y);
	}

	private static TextureData toTexture(GLProfile glProfile, BufferedImage img) {
		try {
			final ByteArrayOutputStream output = new ByteArrayOutputStream() {
				@Override
				public synchronized byte[] toByteArray() {
					return this.buf;
				}
			};
			ImageIO.write(img, "png", output);
			return TextureIO.newTextureData(glProfile, new ByteArrayInputStream(output.toByteArray(), 0, output.size()), true, "png");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
