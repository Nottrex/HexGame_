package client.game;

import client.audio.AudioHandler;
import client.audio.AudioPlayer;
import client.game.gameView.GameView;
import client.game.overlay.ESC_Overlay;
import client.game.overlay.OptionsOverlay;
import client.game.overlay.Overlay;
import client.i18n.LanguageHandler;
import client.window.GUIConstants;
import client.window.KeyBindings;
import client.window.TextureHandler;
import client.window.Window;
import client.window.components.ImageButton;
import client.window.components.ImageTextLabel;
import client.window.components.TextLabel;
import client.window.view.View;
import client.window.view.ViewErrorScreen;
import client.window.view.ViewMainMenu;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import game.Location;
import game.Unit;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitState;
import game.enums.UnitType;
import game.map.GameMap;
import game.util.ActionUtil;
import game.util.PossibleActions;
import networking.client.ClientListener;
import networking.gamePackets.clientPackets.PacketClientKicked;
import networking.packets.Packet;
import server.ServerMain;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ViewGame extends View implements ClientListener {
	private static final long DOUBLEPRESSTIME = 250;

	private MouseInputListener mouseListener;
	private KeyInputListener keyListener;

	private Camera cam;
	private Window window;
	private Controller controller;
	private ServerMain server;

	private JPanel bottom;
	private GameView center;

	private ImageButton button_audioOn;
	private ImageButton button_musicOn;
	private ImageButton button_centerCamera;
	private ImageButton button_endTurn;
	private ImageButton button_backToMainMenu;
	private ImageButton button_nextUnit;
	private ImageTextLabel topBar;
	private TextLabel fpsLabel;

	private AudioPlayer audioPlayer;
	private boolean audioOn = true;
	private boolean musicOn = true;

	private int width, height;

	private Overlay overlay;
	private boolean startCenterCamera = false;
	private long lastClick;
	private boolean drawing2 = false;

	public ViewGame(ServerMain server) {
		this.server = server;
	}

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;
		controller.setViewPacketListener(this);

		cam = new Camera();

		loadResources();

		mouseListener = new MouseInputListener(this);
		keyListener = new KeyInputListener(this);

		width = window.getWidth();
		height = window.getHeight();

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		center = new GameView(glcapabilities, controller, cam);

		center.addMouseWheelListener(mouseListener);
		center.addMouseMotionListener(mouseListener);
		center.addMouseListener(mouseListener);
		center.addKeyListener(keyListener);

		bottom = new JPanel(null);
		bottom.setPreferredSize(new Dimension(800, 100));
		bottom.setBackground(GUIConstants.COLOR_INFOBAR_BACKGROUND);

		center.setBackground(GUIConstants.COLOR_GAME_BACKGROUND);
		button_audioOn = new ImageButton(window, TextureHandler.getImagePng("button_audioOn"), e -> onKeyType(KeyBindings.KEY_TOGGLE_AUDIO));
		button_musicOn = new ImageButton(window, TextureHandler.getImagePng("button_musicOn"), e -> onKeyType(KeyBindings.KEY_TOGGLE_MUSIC));
		button_centerCamera = new ImageButton(window, TextureHandler.getImagePng("button_centerCamera"), e -> onKeyType(KeyBindings.KEY_CENTER_CAMERA));
		button_endTurn = new ImageButton(window, TextureHandler.getImagePng("button_endTurn"), e -> onKeyType(KeyBindings.KEY_NEXT_PLAYER));
		button_nextUnit = new ImageButton(window, TextureHandler.getImagePng("button_nextUnit"), e -> onKeyType(KeyBindings.KEY_NEXT_UNIT));
		button_backToMainMenu = new ImageButton(window, TextureHandler.getImagePng("button_leave"), e -> {
			onLeave();
			if (server != null) server.stop();
			window.updateView(new ViewMainMenu());
		});
		fpsLabel = new TextLabel(() -> ("FPS: " + (int) center.animator.getLastFPS()), false);
		topBar = new ImageTextLabel(new ImageTextLabel.ImageText() {
			@Override
			public BufferedImage getImage() {
				return TextureHandler.getImagePng(controller.game == null ? "" : "bar_" + controller.game.getPlayerColor().toString().toLowerCase());
			}

			@Override
			public String getText() {
				return String.format(LanguageHandler.get("Round") + " %d   %d / %d   %s   %s", controller.game == null ? 1 : controller.game.getRound(), controller.game == null ? 1 : controller.game.getPlayerTurnID(), controller.game == null ? 1 : controller.game.getPlayerAmount(), controller.game == null ? 1 : controller.game.getPlayerTurn(), controller.game == null ? "" :
						LanguageHandler.get(controller.game.getPlayerColor().getDisplayName()));
			}
		});

		unhideButtons();
		center.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int height = center.getHeight();
				int width = center.getWidth();
				int height2 = (height + width) / 2;
				int buttonHeight = height2 / 80 + 23;
				int barHeight = height2 / 80 + 23;

				button_audioOn.setBounds(width - buttonHeight - 5, 5, buttonHeight, buttonHeight);
				button_musicOn.setBounds(width - buttonHeight * 2 - 5 * 2, 5, buttonHeight, buttonHeight);
				button_centerCamera.setBounds(width - buttonHeight * 3 - 5 * 3, 5, buttonHeight, buttonHeight);
				button_nextUnit.setBounds(width - buttonHeight * 4 - 5 * 4, 5, buttonHeight, buttonHeight);
				button_backToMainMenu.setBounds(5, 5, buttonHeight, buttonHeight);
				topBar.setBounds((width - (380 * barHeight) / 49) / 2, 5, (380 * barHeight) / 49, barHeight);
				fpsLabel.setBounds(10 + buttonHeight, 5, barHeight * 5, barHeight);

				button_endTurn.setBounds(width - buttonHeight - 5, center.getHeight() - buttonHeight - 5, buttonHeight, buttonHeight);

				if (overlay instanceof ESC_Overlay) ((ESC_Overlay) overlay).changeSize();
				else if (overlay instanceof OptionsOverlay) ((OptionsOverlay) overlay).changeSize();
			}
		});

		window.getPanel().setLayout(new BorderLayout());
		window.getPanel().add(center, BorderLayout.CENTER);
		window.getPanel().add(bottom, BorderLayout.PAGE_END);
		window.getPanel().updateUI();

		audioPlayer = new AudioPlayer("EP", Clip.LOOP_CONTINUOUSLY);
		audioPlayer.start();

		centerCamera();
	}

	public GLJPanel getCenter() {
		return center;
	}

	@Override
	public void changeSize() {

		if (width == window.getWidth() && height == window.getHeight()) return;

		width = window.getWidth();
		height = window.getHeight();
	}

	private void centerCamera() {
		GameMap m = controller.game.getMap();
		float[] pos = center.hexPositionToWorldPosition(new Location(m.getWidth() / 2, m.getHeight() / 2 + 1));

		if (startCenterCamera) {
			cam.setZoomSmooth(2.2f / m.getHeight(), GUIConstants.CAMERA_TIME);
			cam.setPositionSmooth(pos[0], pos[1], GUIConstants.CAMERA_TIME);
		} else {
			cam.setZoom(2.2f / m.getHeight());
			cam.setPosition(pos[0], pos[1]);
			startCenterCamera = true;
		}
	}

	public void onMouseClick(int state, int x, int y) {
		float[] point = center.screenPositionToWorldPosition(x, y);
		if (System.currentTimeMillis() - lastClick < DOUBLEPRESSTIME) {
			cam.setZoomSmooth(2.2f / 20, GUIConstants.CAMERA_TIME);
			cam.setPositionSmooth(point[0], point[1], GUIConstants.CAMERA_TIME);
		}

		if (overlay != null && overlay.destroyable()) setOverlay(null);

		controller.onMouseClick(center.getHexFieldPosition(point[0], point[1]));
		redrawInfoBar();
		lastClick = System.currentTimeMillis();
	}

	public void onMouseDrag(int x1, int y1, int x2, int y2) {
		float[] zero = center.screenPositionToWorldPosition(x1, y1);
		float[] one = center.screenPositionToWorldPosition(x2, y2);

		if (overlay != null && overlay.destroyable()) setOverlay(null);
		cam.setPosition(cam.getX() + (-one[0] + zero[0]), cam.getY() + (-one[1] + zero[1]));
	}

	public void onKeyType(int keyCode) {
		if (keyCode == KeyBindings.KEY_CENTER_CAMERA) {
			centerCamera();
		}

		if (keyCode == KeyBindings.KEY_TOGGLE_AUDIO) {
			audioOn = !audioOn;

			if (audioOn) {
				button_audioOn.setImage(TextureHandler.getImagePng("button_audioOn"));
			} else {
				button_audioOn.setImage(TextureHandler.getImagePng("button_audioOff"));
			}
		}

		if (keyCode == KeyBindings.KEY_TOGGLE_MUSIC) {
			musicOn = !musicOn;

			if (musicOn) {
				audioPlayer.resume();
				button_musicOn.setImage(TextureHandler.getImagePng("button_musicOn"));
			} else {
				audioPlayer.pause();
				button_musicOn.setImage(TextureHandler.getImagePng("button_musicOff"));
			}
		}

		if (keyCode == KeyBindings.KEY_NEXT_UNIT) {
			List<Unit> units = controller.game.getMap().activePlayerUnits(controller.game.getPlayerColor());
			if (units.isEmpty()) return;

			int i = 0;
			if (controller.selectedField != null) {
				Optional<Unit> selected = controller.game.getMap().getUnitAt(controller.selectedField);
				if (selected.isPresent() && units.contains(selected.get())) {
					i = (units.indexOf(selected.get()) + 1) % units.size();
				}
			}
			int j = i;
			while (true) {
				Unit u = units.get(i);

				PossibleActions pa = ActionUtil.getPossibleActions(controller.game, u);

				if (pa.canMoveTo().isEmpty() && pa.canAttack().isEmpty()) {
					i = (i + 1) % units.size();
				} else {
					break;
				}

				if (i == j) return;
			}

			Unit u = units.get(i);

			float[] pos = center.hexPositionToWorldPosition(new Location(u.getX(), u.getY()));

			controller.selectedField = null;
			controller.onMouseClick(new Location(u.getX(), u.getY()));

			cam.setPositionSmooth(pos[0], pos[1], GUIConstants.CAMERA_TIME);
			//cam.tzoom = 2.2f/15;
		}

		if (keyCode == KeyBindings.KEY_RAISE_TILT) {
			cam.raiseTilt();
		}

		if (keyCode == KeyBindings.KEY_DECREASE_TILT) {
			cam.decreaseTilt();
		}

		if (keyCode == KeyBindings.KEY_RESET_TILT) {
			cam.setTiltSmooth(0, GUIConstants.CAMERA_TIME);
		}

		if (keyCode == KeyEvent.VK_1) {
			Random r = new Random();
			controller.spawnUnit(new Unit(controller.game.getPlayerColor(), UnitType.INFANTERIE, UnitState.INACTIVE, r.nextInt(50), r.nextInt(50)));
		}

		if (keyCode == KeyEvent.VK_ESCAPE) {
			if (!(overlay instanceof ESC_Overlay)) {
				hideButtons();
				setOverlay(new ESC_Overlay(window, this));
				changeSize();
			} else {
				unhideButtons();
				setOverlay(null);
				changeSize();
			}
		}

		if (keyCode == KeyEvent.VK_L) {
			cam.addScreenshake(0.01f);
		}

		controller.onKeyType(keyCode);
	}

	public void onMouseWheel(double d) {
		double a = 1;
		if (d < 0) {
			a = GUIConstants.ZOOM;
		}
		if (d > 0) {
			a = 1 / GUIConstants.ZOOM;
		}

		if (overlay != null && overlay.destroyable()) setOverlay(null);
		cam.zoomSmooth((float) a);
	}

	public void redrawInfoBar() {
		if (bottom == null || bottom.getWidth() <= 0 || bottom.getHeight() <= 0 || controller == null || controller.game == null || drawing2)
			return;
		drawing2 = true;

		BufferedImage buffer2 = new BufferedImage(bottom.getWidth(), bottom.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		GameMap m = controller.game.getMap();

		Graphics g = buffer2.getGraphics();
		g.setColor(GUIConstants.COLOR_INFOBAR_BACKGROUND);
		g.fillRect(0, 0, bottom.getWidth(), bottom.getHeight());


		g.setColor(Color.WHITE);
		int lx = (bottom.getWidth() - 800) / 2;

		float[] point = center.screenPositionToWorldPosition(mouseListener.getMouseX(), mouseListener.getMouseY());
		controller.hoverField = center.getHexFieldPosition(point[0], point[1]);
		Location mouseLocation = controller.hoverField;

		if (mouseLocation != null) {
			Field f = m.getFieldAt(mouseLocation);

			if (f != Field.VOID) {
				g.drawImage(TextureHandler.getImagePng("field_" + f.toString().toLowerCase() + "_" + m.getDiversityAt(mouseLocation)), lx + 5, 10, (int) (90 / GUIConstants.HEX_TILE_XY_RATIO), 90, null);
				g.drawString(LanguageHandler.get("Costs") + ": " + f.getMovementCost(), lx + 10 + (int) (90 / GUIConstants.HEX_TILE_XY_RATIO), 60);
			}

			g.drawString(String.format("x: %d    y: %d", mouseLocation.x, mouseLocation.y), lx + 10 + (int) (90 / GUIConstants.HEX_TILE_XY_RATIO), 20);
			g.drawString(LanguageHandler.get(f.getDisplayName()), lx + 10 + (int) (90 / GUIConstants.HEX_TILE_XY_RATIO), 40);

			Optional<Unit> unit = m.getUnitAt(mouseLocation);

			if (unit.isPresent()) {
				Unit u = unit.get();

				g.drawImage(TextureHandler.getImagePng("unit_" + u.getPlayer().toString().toLowerCase() + "_" + u.getType().toString().toLowerCase()), lx + 800 / 4 + 5, 20, (int) (u.getType().getSize() * 90), (int) (u.getType().getSize() * 90 * GUIConstants.UNIT_XY_RATIO), null);

				g.drawString(LanguageHandler.get(u.getType().getDisplayName()), lx + 800 / 4 + 5 + (int) (u.getType().getSize() * 90) + 10, 30);
				g.drawString(LanguageHandler.get(u.getPlayer().getDisplayName()), lx + 800 / 4 + 5 + (int) (u.getType().getSize() * 90) + 10, 50);
				g.drawString(LanguageHandler.get("Movementrange") + ": " + u.getType().getMovementDistance(), lx + 800 / 4 + 5 + (int) (u.getType().getSize() * 90) + 10, 70);
				g.drawString(LanguageHandler.get("Attackrange") + ": " + u.getType().getMinAttackDistance() + "-" + u.getType().getMaxAttackDistance(), lx + 800 / 4 + 5 + (int) (u.getType().getSize() * 90) + 10, 90);
			}
		}

		if (controller.selectedField != null) {
			Field f = m.getFieldAt(controller.selectedField);

			if (f != Field.VOID) {
				g.drawImage(TextureHandler.getImagePng("field_" + f.toString().toLowerCase() + "_" + m.getDiversityAt(controller.selectedField)), 400 + lx + 5, 10, (int) (90 / GUIConstants.HEX_TILE_XY_RATIO), 90, null);
				g.drawString(LanguageHandler.get("Costs") + ": " + +f.getMovementCost(), 400 + lx + 10 + (int) (90 / GUIConstants.HEX_TILE_XY_RATIO), 60);
			}

			g.drawString(String.format("x: %d    y: %d", controller.selectedField.x, controller.selectedField.y), 400 + lx + 10 + (int) (90 / GUIConstants.HEX_TILE_XY_RATIO), 20);
			g.drawString(LanguageHandler.get(f.getDisplayName()), 400 + lx + 10 + (int) (90 / GUIConstants.HEX_TILE_XY_RATIO), 40);

			Optional<Unit> unit = m.getUnitAt(controller.selectedField);

			if (unit.isPresent()) {
				Unit u = unit.get();

				g.drawImage(TextureHandler.getImagePng("unit_" + u.getPlayer().toString().toLowerCase() + "_" + u.getType().toString().toLowerCase()), 400 + lx + 800 / 4 + 5, 20, (int) (u.getType().getSize() * 90), (int) (u.getType().getSize() * 90 * GUIConstants.UNIT_XY_RATIO), null);

				g.drawString(LanguageHandler.get(u.getType().getDisplayName()), 400 + lx + 800 / 4 + 5 + (int) (u.getType().getSize() * 90) + 10, 30);
				g.drawString(LanguageHandler.get(u.getPlayer().getDisplayName()), 400 + lx + 800 / 4 + 5 + (int) (u.getType().getSize() * 90) + 10, 50);
				g.drawString(LanguageHandler.get("Movementrange") + ": " + u.getType().getMovementDistance(), 400 + lx + 800 / 4 + 5 + (int) (u.getType().getSize() * 90) + 10, 70);
				g.drawString(LanguageHandler.get("Attackrange") + ": " + u.getType().getMinAttackDistance() + "-" + u.getType().getMaxAttackDistance(), 400 + lx + 800 / 4 + 5 + (int) (u.getType().getSize() * 90) + 10, 90);
			}
		}

		g.setColor(Color.BLACK);
		g.drawLine(lx + 400, 0, lx + 400, 100);

		bottom.getGraphics().drawImage(buffer2, 0, 0, null);
		drawing2 = false;
	}

	@Override
	public void stop() {
		center.destroy();
		center.removeMouseWheelListener(mouseListener);
		center.removeMouseMotionListener(mouseListener);
		center.removeMouseListener(mouseListener);
		center.removeKeyListener(keyListener);
		if (audioPlayer != null) audioPlayer.stop();
		controller.stopConnection();
	}

	private void loadResources() {
		TextureHandler.loadImagePngSpriteSheet("field", "fields/fields");
		TextureHandler.loadImagePngSpriteSheet("fieldmarker", "fieldmarker/fieldmarker");
		TextureHandler.loadImagePngSpriteSheet("arrow", "arrow/arrow");
		TextureHandler.loadImagePngSpriteSheet("unit", "units/units");

		for (PlayerColor pc : PlayerColor.values()) {
			TextureHandler.loadImagePng("bar_" + pc.toString().toLowerCase(), "ui/bar/bar_" + pc.toString().toLowerCase());
		}

		TextureHandler.loadImagePng("button_audioOn", "ui/buttons/audioOn");
		TextureHandler.loadImagePng("button_audioOff", "ui/buttons/audioOff");

		TextureHandler.loadImagePng("button_musicOn", "ui/buttons/musicOn");
		TextureHandler.loadImagePng("button_musicOff", "ui/buttons/musicOff");

		TextureHandler.loadImagePng("button_centerCamera", "ui/buttons/centerCamera");

		TextureHandler.loadImagePng("button_endTurn", "ui/buttons/endTurn");
		TextureHandler.loadImagePng("button_nextUnit", "ui/buttons/next");

		TextureHandler.loadImagePng("button_leave", "ui/buttons/exit");

		AudioHandler.loadMusicWav("EP", "music/EP");
	}

	public void setOverlay(Overlay ov) {
		if (overlay != null) center.remove(overlay);
		this.overlay = ov;
		if (ov != null) {
			center.add(ov);
		}
	}


	@Override
	public void onReceivePacket(Packet p) {
		if (p instanceof PacketClientKicked) {
			controller.stopConnection();
			window.updateView(new ViewErrorScreen(((PacketClientKicked) p).getReason()));
		}
	}

	@Override
	public void onLeave() {
		if (window.isCurrentView(this)) {
			controller.stopConnection();
			window.updateView(new ViewErrorScreen("Connection lost!"));
		}
	}

	public void hideButtons() {
		center.remove(button_audioOn);
		center.remove(button_musicOn);
		center.remove(button_centerCamera);
		center.remove(button_endTurn);
		center.remove(button_backToMainMenu);
		center.remove(button_nextUnit);
		center.remove(topBar);
		center.remove(fpsLabel);
	}

	public void unhideButtons() {
		center.add(button_audioOn);
		center.add(button_musicOn);
		center.add(button_centerCamera);
		center.add(button_endTurn);
		center.add(button_backToMainMenu);
		center.add(button_nextUnit);
		center.add(topBar);
		center.add(fpsLabel);
	}

	public int getBottomHeigth() {
		return bottom != null ? bottom.getHeight() : 0;
	}

	public AudioPlayer getPlayer() {
		return audioPlayer;
	}
}
