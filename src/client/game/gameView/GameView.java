package client.game.gameView;

import client.game.Camera;
import client.game.Controller;
import client.game.gameView.shader.*;
import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.animationActions.AnimationAction;
import client.window.animationActions.AnimationActionUnitMove;
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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

public class GameView extends GLJPanel implements GLEventListener {
	private static final float[][] vertexPos = {
			{0.0f, 0.25f}, {0.0f, 0.75f}, {0.5f, 1f}, {1.0f, 0.75f}, {1.0f, 0.25f}, {0.5f, 0f}, {0.5f, 0.5f}
	};
	public FPSAnimator animator;
	private Controller controller;
	private Camera cam;
	private IntBuffer buffers = IntBuffer.allocate(4);
	private IntBuffer vao = IntBuffer.allocate(1);
	private IntBuffer vao2 = IntBuffer.allocate(1);
	private int length;
	private Texture fieldTexture;
	private FieldShader fieldShader;
	private Texture fieldmarkerTexture;
	private FieldmarkerShader fieldmarkerShader;
	private HealthBarShader healthBarShader;
	private Texture arrowTexture;
	private SquareShader squareShader;
	private Texture unitTexture;
	private UnitShader unitShader;
	private float[] projectionMatrix;
	private float[] viewMatrix = null;
	private float[] cameraPosition = null;

	public GameView(GLCapabilities capabilities, Controller controller, Camera cam) {
		super(capabilities);
		this.controller = controller;
		this.cam = cam;
		setFocusable(true);
		this.addGLEventListener(this);
	}

	private static void drawMovementArrow(GL2 gl, SquareShader squareShader, Location location, Direction d) {
		double wx = 1;
		double wy = GUIConstants.HEX_TILE_XY_RATIO;
		double centerY1 = -((location.y) * (GUIConstants.HEX_TILE_YY_RATIO) * wy - wy / 2);
		double centerX1 = (location.x) * wx - (location.y) * wy / (2 * GUIConstants.HEX_TILE_XY_RATIO) + wx / 2;

		double centerY2 = -((location.y + d.getYMovement()) * (GUIConstants.HEX_TILE_YY_RATIO) * wy - wy / 2);
		double centerX2 = (location.x + d.getXMovement()) * wx - (location.y + d.getYMovement()) * wy / (2 * GUIConstants.HEX_TILE_XY_RATIO) + wx / 2;

		float[] position = null;
		Rectangle spriteSheetPosition = null;
		switch (d) {
			case RIGHT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_right");
				position = new float[]{(float) (centerX1 + wx * GUIConstants.ARROW_SIZE / 2), (float) (centerY2 - wx * GUIConstants.ARROW_SIZE / 2), (float) (wx * GUIConstants.ARROW_SIZE), (float) (GUIConstants.ARROW_SIZE * wx)};
				break;
			case LEFT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_left");
				position = new float[]{(float) (centerX2 + wx * GUIConstants.ARROW_SIZE / 2), (float) (centerY2 - wx * GUIConstants.ARROW_SIZE / 2), (float) (wx * GUIConstants.ARROW_SIZE), (float) (GUIConstants.ARROW_SIZE * wx)};
				break;
			case UP_RIGHT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_up_right");
				position = new float[]{(float) (centerX1 + ((centerX2 - centerX1) - wx * GUIConstants.ARROW_SIZE) / 2), (float) (centerY1 + ((centerY2 - centerY1) - wx * GUIConstants.ARROW_SIZE) / 2), (float) (wx * GUIConstants.ARROW_SIZE), (float) (wx * GUIConstants.ARROW_SIZE)};
				break;
			case UP_LEFT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_up_left");
				position = new float[]{(float) (centerX1 + ((centerX2 - centerX1) - wx * GUIConstants.ARROW_SIZE) / 2), (float) (centerY1 + ((centerY2 - centerY1) - wx * GUIConstants.ARROW_SIZE) / 2), (float) (wx * GUIConstants.ARROW_SIZE), (float) (wx * GUIConstants.ARROW_SIZE)};
				break;
			case DOWN_LEFT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_down_left");
				position = new float[]{(float) (centerX1 + ((centerX2 - centerX1) - wx * GUIConstants.ARROW_SIZE) / 2), (float) (centerY1 + ((centerY2 - centerY1) - wx * GUIConstants.ARROW_SIZE) / 2), (float) (wx * GUIConstants.ARROW_SIZE), (float) (wx * GUIConstants.ARROW_SIZE)};
				break;
			case DOWN_RIGHT:
				spriteSheetPosition = TextureHandler.getSpriteSheetBounds("arrow_arrow_down_right");
				position = new float[]{(float) (centerX1 + ((centerX2 - centerX1) - wx * GUIConstants.ARROW_SIZE) / 2), (float) (centerY1 + ((centerY2 - centerY1) - wx * GUIConstants.ARROW_SIZE) / 2), (float) (wx * GUIConstants.ARROW_SIZE), (float) (wx * GUIConstants.ARROW_SIZE)};
				break;
			default:
				break;
		}

		squareShader.setTextureSheetBounds(gl, spriteSheetPosition.x, spriteSheetPosition.y, spriteSheetPosition.width, spriteSheetPosition.height);
		squareShader.setBounds(gl, position[0], position[1], position[2], position[3]);
		gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		drawable.setGL((new DebugGL2(gl)));

		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.setSwapInterval(1);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glClearColor(0f, 1f, 1f, 1f);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

		setupFieldShader(gl);

		fieldmarkerShader = new FieldmarkerShader(gl);

		fieldmarkerShader.start(gl);
		fieldmarkerShader.setHexWidth(gl, 1f);
		fieldmarkerShader.setHexHeight(gl, (float) GUIConstants.HEX_TILE_YY_RATIO);
		fieldmarkerShader.setHexHeight2(gl, (float) GUIConstants.HEX_TILE_XY_RATIO);
		fieldmarkerShader.setTexture(gl, 0);
		BufferedImage img = TextureHandler.getImagePng("fieldmarker");
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

		healthBarShader = new HealthBarShader(gl);

		healthBarShader.start(gl);
		healthBarShader.stop(gl);

		textureInit(gl);

		animator = new FPSAnimator(this, 60);
		animator.setUpdateFPSFrames(60, null);
		animator.start();
		requestFocus();
	}

	private void setupFieldShader(GL2 gl) {
		fieldShader = new FieldShader(gl);

		BufferedImage img = TextureHandler.getImagePng("field");
		float hexHeight = (float) GUIConstants.HEX_TILE_YY_RATIO;
		float hexHeight2 = (float) GUIConstants.HEX_TILE_XY_RATIO;
		float hexWidth = 1f;
		float tw = img.getWidth();
		float th = img.getHeight();

		Rectangle fogBounds = TextureHandler.getSpriteSheetBounds("field_fog");

		fieldShader.start(gl);
		fieldShader.setTexture(gl, 0);
		fieldShader.setFogBounds(gl, fogBounds.x / tw, fogBounds.y / th, fogBounds.width / tw, fogBounds.height / th);
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

		float[] locations = new float[length * 2 * vertexPos.length];
		float[] texLocations = new float[length * 2 * vertexPos.length];
		byte[] fieldData = new byte[length * vertexPos.length];
		int[] indices = new int[length * 18];

		int a = 0;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (map.getFieldAt(x, y).isAccessible()) {
					Field f = map.getFieldAt(x, y);
					Rectangle rec = TextureHandler.getSpriteSheetBounds("field_" + f.toString().toLowerCase() + "_" + map.getDiversityAt(x, y));

					for (int i = 0; i < vertexPos.length; i++) {
						texLocations[a * vertexPos.length * 2 + 2 * i] = (rec.x + vertexPos[i][0] * rec.width) / tw;
						texLocations[a * vertexPos.length * 2 + 2 * i + 1] = 1 - (rec.y + (1 - vertexPos[i][1]) * rec.height) / th;

						locations[a * vertexPos.length * 2 + 2 * i] = (vertexPos[i][0] + x - y / 2.0f) * hexWidth;
						locations[a * vertexPos.length * 2 + 2 * i + 1] = (vertexPos[i][1]) * hexHeight2 - (y) * hexHeight2 * hexHeight;

						fieldData[a * vertexPos.length + i] = 0 | (0) | (0 << 2);
					}

					indices[a * 18] = a * 7;
					indices[a * 18 + 1] = a * 7 + 6;
					indices[a * 18 + 2] = a * 7 + 1;

					indices[a * 18 + 3] = a * 7 + 1;
					indices[a * 18 + 4] = a * 7 + 6;
					indices[a * 18 + 5] = a * 7 + 2;

					indices[a * 18 + 6] = a * 7 + 2;
					indices[a * 18 + 7] = a * 7 + 6;
					indices[a * 18 + 8] = a * 7 + 3;

					indices[a * 18 + 9] = a * 7 + 3;
					indices[a * 18 + 10] = a * 7 + 6;
					indices[a * 18 + 11] = a * 7 + 4;

					indices[a * 18 + 12] = a * 7 + 4;
					indices[a * 18 + 13] = a * 7 + 6;
					indices[a * 18 + 14] = a * 7 + 5;

					indices[a * 18 + 15] = a * 7 + 5;
					indices[a * 18 + 16] = a * 7 + 6;
					indices[a * 18 + 17] = a * 7;

					a++;
				}
			}
		}

		FloatBuffer locationBuffer = FloatBuffer.wrap(locations);
		FloatBuffer texLocationBuffer = FloatBuffer.wrap(texLocations);
		ByteBuffer fieldDataBuffer = ByteBuffer.wrap(fieldData);
		IntBuffer indicesBuffer = IntBuffer.wrap(indices);

		gl.glGenBuffers(4, buffers);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, 4 * length * 7 * 2, locationBuffer, GL2.GL_STATIC_DRAW);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(1));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, 4 * length * 7 * 2, texLocationBuffer, GL2.GL_STATIC_DRAW);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(2));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, 1 * length * 7, fieldDataBuffer, GL2.GL_STATIC_DRAW);

		gl.glGenVertexArrays(1, vao);
		gl.glBindVertexArray(vao.get(0));

		gl.glEnableVertexAttribArray(fieldShader.getLocationLocation());
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glVertexAttribPointer(fieldShader.getLocationLocation(), 2, GL.GL_FLOAT, false, 0, 0);

		gl.glEnableVertexAttribArray(fieldShader.getTexLocationLocation());
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(1));
		gl.glVertexAttribPointer(fieldShader.getTexLocationLocation(), 2, GL.GL_FLOAT, false, 0, 0);

		gl.glEnableVertexAttribArray(fieldShader.getFieldDataLocation());
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(2));
		gl.glVertexAttribPointer(fieldShader.getFieldDataLocation(), 1, GL.GL_BYTE, false, 0, 0);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, buffers.get(3));
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, 18 * length * 4, indicesBuffer, GL.GL_STATIC_DRAW);

		gl.glGenVertexArrays(1, vao2);
		gl.glBindVertexArray(vao2.get(0));
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
		healthBarShader.cleanUp(gl);
		fieldmarkerShader.cleanUp(gl);
		squareShader.cleanUp(gl);
		unitShader.cleanUp(gl);
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		long time = (System.currentTimeMillis() % 100000000);

		controller.updateAnimationActions();
		AnimationAction currentAnimation = controller.getAnimationAction();
		updateCamera(gl, cam);

		fieldTexture.bind(gl);
		fieldShader.start(gl);
		fieldShader.setTime(gl, (float) time);

		gl.glBindVertexArray(vao.get(0));
		gl.glDrawElements(GL.GL_TRIANGLES, length * 18, GL.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(vao2.get(0));
		fieldShader.stop(gl);


		GameMap map = controller.game.getMap();

		fieldmarkerTexture.bind(gl);
		fieldmarkerShader.start(gl);
		fieldmarkerShader.setTime(gl, (float) time);

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

				for (Location target : pa.canMoveTo()) {
					fieldmarkerShader.setLocation(gl, target.x, target.y);
					rec = TextureHandler.getSpriteSheetBounds("fieldmarker_normalBlue");
					fieldmarkerShader.setTextureSheetBounds(gl, rec.x, rec.y, rec.width, rec.height);
					gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 6);
				}

				for (Location target : pa.canAttack()) {
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
					squareShader.setTime(gl, (float) time);

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
		unitShader.setTime(gl, (float) time);
		for (Unit unit : map.getUnits()) {
			UnitType ut = unit.getType();

			double[] pos = getUnitPosition(unit, currentAnimation);
			unitShader.setBounds(gl, (float) pos[0], (float) pos[1], (float) pos[2], (float) pos[3]);
			Rectangle rec = TextureHandler.getSpriteSheetBounds("unit_" + unit.getPlayer().toString().toLowerCase() + "_" + ut.toString().toLowerCase());
			unitShader.setTextureSheetBounds(gl, rec.x, rec.y, rec.width, rec.height);

			gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
		}

		unitShader.stop(gl);

		healthBarShader.start(gl);
		healthBarShader.setTime(gl, (float) time);
		for (Unit unit : map.getUnits()) {
			double[] pos = getUnitPosition(unit, currentAnimation);

			healthBarShader.setBounds(gl, (float) pos[0] + (float) pos[2] / 6f, (float) pos[1] + (float) pos[3] / 5, (float) pos[2] / 1.5f, (float) pos[3] / 6);
			healthBarShader.setHealth(gl, unit.getHealth() / unit.getType().getHealth());
			gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
		}
		healthBarShader.stop(gl);

		gl.glFlush();
	}

	private double[] getUnitPosition(Unit unit, AnimationAction currentAnimation) {
		UnitType ut = unit.getType();
		double w = ut.getSize();
		double h = w * GUIConstants.UNIT_XY_RATIO;

		double py = -((unit.getY()) * (GUIConstants.HEX_TILE_YY_RATIO) * GUIConstants.HEX_TILE_XY_RATIO - (GUIConstants.HEX_TILE_XY_RATIO - h) / 2);
		double px = unit.getX() - unit.getY() / 2.0 + (1 - w) / 2;

		if (currentAnimation != null && currentAnimation instanceof AnimationActionUnitMove) {
			AnimationActionUnitMove animation = (AnimationActionUnitMove) currentAnimation;

			if (animation.getUnit() == unit) {
				py = -((unit.getY() + animation.getCurrentDirection().getYMovement() * animation.interpolation()) * (GUIConstants.HEX_TILE_YY_RATIO) * GUIConstants.HEX_TILE_XY_RATIO - (GUIConstants.HEX_TILE_XY_RATIO - h) / 2);
				px = (unit.getX() + animation.getCurrentDirection().getXMovement() * animation.interpolation()) - (unit.getY() + animation.getCurrentDirection().getYMovement() * animation.interpolation()) / 2.0 + (1 - w) / 2;
			}
		}
		return new double[]{px, py, w, h};
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		float aspect = width * 1.0f / height;
		float fov = 90;
		float near = 0.01f;
		float far = 10000f;

		projectionMatrix = FloatUtil.makePerspective(new float[16], 0, true, (float) Math.toRadians(fov), aspect, near, far);

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

		healthBarShader.start(gl);
		healthBarShader.setProjectionMatrix(gl, projectionMatrix);
		healthBarShader.stop(gl);

		textureInit(gl);
		gl.glViewport(x, y, width, height);
	}

	private void updateCamera(GL2 gl, Camera cam) {
		boolean b = cam.update();
		if (cam.zoom == Double.POSITIVE_INFINITY || cam.zoom == Double.NEGATIVE_INFINITY || cam.zoom == Double.NaN) {
			cam.setZoom(1);
			b = cam.update();
		}

		if (viewMatrix == null || b) {
			float[] target = {cam.x, cam.y, 0};
			cameraPosition = new float[]{cam.x, cam.y - cam.tilt / cam.zoom, 1 / cam.zoom};

			float[] dir = new float[]{cameraPosition[0] - target[0], cameraPosition[1] - target[1], cameraPosition[2] - target[2]};
			float[] up = new float[3];
			VectorUtil.crossVec3(up, dir, new float[]{1, 0, 0});
			VectorUtil.normalizeVec3(up);

			viewMatrix = FloatUtil.makeLookAt(new float[16], 0, cameraPosition, 0, target, 0, up, 0, new float[16]);

			fieldShader.start(gl);
			fieldShader.setCamera(gl, viewMatrix);
			fieldShader.setCamZ(gl, cameraPosition[2]);
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

			healthBarShader.start(gl);
			healthBarShader.setCamera(gl, viewMatrix);
			healthBarShader.stop(gl);
		}
	}

	public float[] screenPositionToWorldPosition(int x, int y) {
		if (projectionMatrix == null || viewMatrix == null || cam.zoom == Double.POSITIVE_INFINITY || cam.zoom == Double.NEGATIVE_INFINITY || cam.zoom == Double.NaN)
			return new float[]{-1, -1};

		float[] ray_nds = {(x * 1.0f / getWidth()) * 2 - 1, (1 - (y * 1.0f / getHeight()) * 2), 1.0f};
		float[] ray_clip = {ray_nds[0], ray_nds[1], -1.0f, 1.0f};

		float[] ray_eye = FloatUtil.multMatrixVec(FloatUtil.invertMatrix(projectionMatrix, new float[16]), ray_clip, new float[4]);
		ray_eye[2] = -1.0f;
		ray_eye[3] = 0.0f;

		float[] ray_wor4 = FloatUtil.multMatrixVec(FloatUtil.invertMatrix(viewMatrix, new float[16]), ray_eye, new float[4]);
		float[] ray_wor = {ray_wor4[0], ray_wor4[1], ray_wor4[2]};
		ray_wor = VectorUtil.normalizeVec3(ray_wor);

		float distance = -cameraPosition[2] / ray_wor[2];
		float[] point = {distance * ray_wor[0] + cameraPosition[0], distance * ray_wor[1] + cameraPosition[1]};

		return point;
	}

	public float[] hexPositionToWorldPosition(Location hexField) {
		int x = hexField.x;
		int y = hexField.y;

		float fx = x * 1 - y / 2 + 0.5f;
		float fy = (float) (-y * GUIConstants.HEX_TILE_YY_RATIO - GUIConstants.HEX_TILE_XY_RATIO) - (float) GUIConstants.HEX_TILE_YY_RATIO / 2;

		return new float[]{fx, fy};
	}

	public Location getHexFieldPosition(float px, float py) {
		py = -py;
		py += GUIConstants.HEX_TILE_XY_RATIO;
		double dy = py / (GUIConstants.HEX_TILE_YY_RATIO * GUIConstants.HEX_TILE_XY_RATIO);

		int y = (int) Math.floor(dy);
		int x = (int) Math.floor(px + y / 2.0);
		if (((dy % 1) + 1) % 1 <= (1 - GUIConstants.HEX_TILE_YY_RATIO) / (GUIConstants.HEX_TILE_YY_RATIO)) {
			double vx = (((px + y / 2.0) % 1) + 1) % 1;
			double vy = ((((dy % 1) + 1) % 1) / ((1 - GUIConstants.HEX_TILE_YY_RATIO) / (GUIConstants.HEX_TILE_YY_RATIO))) / 2;

			if (vx < 0.5) {
				if (vx + vy < 0.5) {
					x--;
					y--;
				}
			} else {
				vx = 1 - vx;

				if (vx + vy < 0.5) {
					y--;
				}
			}
		}

		return new Location(x, y);
	}

}
