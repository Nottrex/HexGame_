package client.window.view.game;

import client.Controller;
import client.KeyBindings;
import client.audio.AudioHandler;
import client.audio.AudioPlayer;
import client.components.ImageButton;
import client.components.ImageTextLabel;
import client.components.TextLabel;
import client.window.*;
import client.window.Window;
import client.window.view.ViewErrorScreen;
import client.window.view.ViewMainMenu;
import client.window.view.game.gameView.GameView;
import client.window.view.game.KeyInputListener;
import client.window.view.game.MouseInputListener;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import game.Location;
import game.Unit;
import game.enums.Direction;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitType;
import game.map.GameMap;
import networking.client.ClientListener;
import networking.gamePackets.clientPackets.PacketClientKicked;
import networking.packets.Packet;
import server.ServerMain;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class ViewGame extends View implements ClientListener {

	private boolean stop = true;

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
	private ImageTextLabel topBar;
	private TextLabel fpsLabel;

	private AudioPlayer audioPlayer;
	private boolean audioOn = true;
	private boolean musicOn = true;

	private int width, height;

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

		mouseListener 	= new MouseInputListener(this);
		keyListener 	= new KeyInputListener(this);

		width = window.getWidth();
		height = window.getHeight();

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		center = new GameView(glcapabilities, controller, cam);

		center.addMouseWheelListener(mouseListener);
		center.addMouseMotionListener(mouseListener);
		center.addMouseListener(mouseListener);
		center.addKeyListener(keyListener);

		bottom = new JPanel(null);
		bottom.setPreferredSize(new Dimension(800, 100));
		bottom.setBackground(GUIConstants.COLOR_INFOBAR_BACKGROUND);


		center.setBackground(GUIConstants.COLOR_GAME_BACKGROUND);
		button_audioOn = new ImageButton(TextureHandler.getImagePng("button_audioOn"), e -> onKeyType(KeyBindings.KEY_TOGGLE_AUDIO));
		button_musicOn = new ImageButton(TextureHandler.getImagePng("button_musicOn"), e -> onKeyType(KeyBindings.KEY_TOGGLE_MUSIC));
		button_centerCamera = new ImageButton(TextureHandler.getImagePng("button_centerCamera"), e -> onKeyType(KeyBindings.KEY_CENTER_CAMERA));
		button_endTurn = new ImageButton(TextureHandler.getImagePng("button_endTurn"), e -> onKeyType(KeyBindings.KEY_NEXT_PLAYER));
		button_backToMainMenu = new ImageButton(TextureHandler.getImagePng("button_endTurn"), e -> {onLeave(); if(server != null) server.stop(); window.updateView(new ViewMainMenu());});
		fpsLabel = new TextLabel(() -> ("FPS: " + center.animator.getFPS()), false);
		topBar = new ImageTextLabel(new ImageTextLabel.ImageText() {
			@Override
			public BufferedImage getImage() {
				return TextureHandler.getImagePng(controller.game == null ? "" : "bar_" + controller.game.getPlayerColor().toString().toLowerCase());
			}

			@Override
			public String getText() {
				return String.format("Round %d   %d / %d   %s   %s", controller.game == null ? 1 : controller.game.getRound(), controller.game == null ? 1 : controller.game.getPlayerTurnID(), controller.game == null ? 1 : controller.game.getPlayerAmount(), controller.game == null ? 1 : controller.game.getPlayerTurn(), controller.game == null ? "" : controller.game
				.getPlayerColor().getDisplayName());
			}
		});

		center.add(button_audioOn);
		center.add(button_musicOn);
		center.add(button_centerCamera);
		center.add(button_endTurn);
		center.add(button_backToMainMenu);
		center.add(topBar);
		center.add(fpsLabel);
		center.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int height = center.getHeight();
				int width = center.getWidth();
				int height2 = (height+width)/2;
				int buttonHeight = height2 / 80 + 23;
				int barHeight = height2 / 80 + 23;

				button_audioOn.setBounds(width - buttonHeight - 5, 5, buttonHeight, buttonHeight);
				button_musicOn.setBounds(width - buttonHeight*2 - 5*2, 5, buttonHeight, buttonHeight);
				button_centerCamera.setBounds(width - buttonHeight*3 - 5*3, 5, buttonHeight, buttonHeight);
				button_backToMainMenu.setBounds(5, 5*2 + barHeight, buttonHeight, buttonHeight);
				topBar.setBounds((width-(380*barHeight)/49)/2, 5, (380*barHeight)/49, barHeight);
				fpsLabel.setBounds(5, 5, barHeight*5, barHeight);

				button_endTurn.setBounds(width - buttonHeight - 5, center.getHeight() - buttonHeight - 5, buttonHeight, buttonHeight);
			}
		});

		window.getPanel().setLayout(new BorderLayout());
		window.getPanel().add(center, BorderLayout.CENTER);
		window.getPanel().add(bottom, BorderLayout.PAGE_END);
		window.getPanel().updateUI();

		audioPlayer = new AudioPlayer("EP", Clip.LOOP_CONTINUOUSLY);
		audioPlayer.start();

		centerCamera();

		stop = false;
	}

	public GLJPanel getCenter() {
		return center;
	}

	@Override
	public void changeSize() {
			int widthDifference = width - window.getWidth();
			int heightDifference = height - window.getHeight();

			if(widthDifference == 0 && heightDifference == 0) return;

			width = window.getWidth();
			height = window.getHeight();
	}

	private void centerCamera() {
		GameMap m = controller.game.getMap();

		cam.tzoom = (float)((m.getHeight()* GUIConstants.HEX_TILE_XY_RATIO* GUIConstants.HEX_TILE_XY_RATIO) / center.getHeight());

		//cam.ty = (float)((GUIConstants.HEX_TILE_XY_RATIO)/2-cam.tzoom*center.getHeight()/2 + (m.getHeight()/2)* GUIConstants.HEX_TILE_XY_RATIO* GUIConstants.HEX_TILE_YY_RATIO - 20*cam.tzoom);
		//cam.tx = (float)(0.5 - cam.tzoom*center.getWidth()/2 + (m.getWidth()/2) - (m.getHeight()/4));
	}

	public void onMouseClick(int x, int y) {
		float[] point = center.screenPositionToWorldPosition(x, y);
		controller.onMouseClick(center.getHexFieldPosition(point[0], point[1]));
		redrawInfoBar();
	}


	public void onMouseDrag(int dx, int dy) {
		float[] zero = center.screenPositionToWorldPosition(0, 0);
		float[] one = center.screenPositionToWorldPosition(1, 1);

		dy = -dy;

		cam.tx -= (dx*(-one[0]+zero[0]));
		cam.ty -= (dy*(-one[1]+zero[1]));
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

		cam.tzoom *= a;
	}
	/*
	private boolean drawing = false;
	public void draw() {
		if (stop || audioPlayer == null || center == null || center.getWidth() <= 0 || center.getHeight() <= 0 || drawing || controller == null || controller.game == null || controller.game.getMap() == null || cam == null) return;
		drawing = true;
		audioPlayer.updateVolume();
		AnimationAction currentAnimation = controller.getAnimationAction();

		GameMap m = controller.game.getMap();
		{	//Draw In-game stuff
			g.translate((int) -(cam.x/cam.zoom), (int) -(cam.y/cam.zoom));

			double wx = 1/cam.zoom;
			double wy = wx* GUIConstants.HEX_TILE_XY_RATIO;

			for (Unit u: m.getUnits()) {
				UnitType ut = u.getType();
				double w = wx*ut.getSize();
				double h = w*GUIConstants.UNIT_XY_RATIO;

				double py = (u.getY())*(GUIConstants.HEX_TILE_YY_RATIO)*wy + (wy-h)/2;
				double px = (u.getX())*wx - (u.getY())*wy/(2* GUIConstants.HEX_TILE_XY_RATIO) + (wx-w)/2;

				if (currentAnimation != null && currentAnimation instanceof AnimationActionUnitMove) {
					AnimationActionUnitMove animation = (AnimationActionUnitMove) currentAnimation;

					if (animation.getUnit() == u) {
						py = (u.getY() + animation.getCurrentDirection().getYMovement()*animation.interpolation())*(GUIConstants.HEX_TILE_YY_RATIO)*wy + (wy-h)/2;
						px = (u.getX() + animation.getCurrentDirection().getXMovement()*animation.interpolation())*wx - (u.getY() + animation.getCurrentDirection().getYMovement()*animation.interpolation())*wy/(2* GUIConstants.HEX_TILE_XY_RATIO) + (wx-w)/2;
					}
				}

				g.drawImage(TextureHandler.getImagePng("units_" + ut.toString().toLowerCase() + "_" + u.getPlayer().toString().toLowerCase()), (int) px, (int) py, (int) w, (int) h, null);

			}

			Location mloc = getHexFieldPosition(mouseListener.getMouseX(), mouseListener.getMouseY());
			drawHexField(mloc.x, mloc.y, g, TextureHandler.getImagePng("fieldmarker_select"), wx, wy);

			Location selectedField = controller.selectedField;

			if (selectedField != null) {
				drawHexField(selectedField.x, selectedField.y, g, TextureHandler.getImagePng("fieldmarker_select2"), wx, wy);

				Optional<Unit> u = m.getUnitAt(selectedField);

				if (u.isPresent()) {

					if (controller.pa == null) {
						controller.pa = ActionUtil.getPossibleActions(controller.game, u.get());
					}

					for (Location target: controller.pa.canMoveTo()) {
						drawHexField(target.x, target.y, g, TextureHandler.getImagePng("fieldmarker_blue"), wx, wy);
					}

					for (Location target: controller.pa.canAttack()) {
						drawHexField(target.x, target.y, g, TextureHandler.getImagePng("fieldmarker_red"), wx, wy);
					}

					if (controller.pa.canMoveTo().contains(mloc)) {
						java.util.List<Direction> movements = controller.pa.moveTo(mloc);
						Location a = selectedField;

						for (Direction d: movements) {
							drawMovementArrow(a.x, a.y, g, d, wx, wy);
							a = d.applyMovement(a);
						}
					} else if (controller.pa.canAttack().contains(mloc)) {
						java.util.List<Direction> movements = controller.pa.moveToToAttack(mloc);
						Location a = selectedField;

						for (Direction d: movements) {
							drawMovementArrow(a.x, a.y, g, d, wx, wy);
							a = d.applyMovement(a);
						}
					}
				}
			}

			g.translate((int) (cam.x/cam.zoom), (int) (cam.y/cam.zoom));
		}

		//Draw gui stuff
		for (Component c: center.getComponents()) {
			g.translate(c.getX(), c.getY());

			c.update(g);

			g.translate(-c.getX(), -c.getY());
		}

		//center.getGraphics().drawImage(buffer, 0, 0, null);
		drawing = false;
	}*/

	private boolean drawing2 = false;
	public void redrawInfoBar() {
		if (bottom == null || bottom.getWidth() <= 0 || bottom.getHeight() <= 0 || controller == null || controller.game == null || drawing2) return;
		drawing2 = true;

		BufferedImage buffer2 = new BufferedImage(bottom.getWidth(), bottom.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		GameMap m = controller.game.getMap();

		Graphics g = buffer2.getGraphics();
		g.setColor(GUIConstants.COLOR_INFOBAR_BACKGROUND);
		g.fillRect(0, 0, bottom.getWidth(), bottom.getHeight());


		g.setColor(Color.WHITE);
		int lx = (bottom.getWidth()-800)/2;

		float[] point = center.screenPositionToWorldPosition(mouseListener.getMouseX(), mouseListener.getMouseY());
		controller.hoverField = center.getHexFieldPosition(point[0], point[1]);
		Location mouseLocation = controller.hoverField;

		if (mouseLocation != null) {
			Field f = m.getFieldAt(mouseLocation);

			if (f != Field.VOID) {
				g.drawImage(TextureHandler.getImagePng("field_" + f.toString().toLowerCase()), lx + 5, 10, (int) (90/ GUIConstants.HEX_TILE_XY_RATIO), 90, null);
				g.drawString("Costs: " + f.getMovementCost(), lx + 10 + (int) (90/ GUIConstants.HEX_TILE_XY_RATIO), 60);
			}

			g.drawString(String.format("x: %d    y: %d", mouseLocation.x, mouseLocation.y), lx + 10 + (int) (90/ GUIConstants.HEX_TILE_XY_RATIO), 20);
			g.drawString(f.getDisplayName(), lx + 10 + (int) (90/ GUIConstants.HEX_TILE_XY_RATIO), 40);

			Optional<Unit> unit = m.getUnitAt(mouseLocation);

			if (unit.isPresent()) {
				Unit u = unit.get();

				g.drawImage(TextureHandler.getImagePng("units_" + u.getType().toString().toLowerCase() + "_" + u.getPlayer().toString().toLowerCase()), lx + 800/4 + 5, 20, (int) (u.getType().getSize()*90), (int) (u.getType().getSize()*90*GUIConstants.UNIT_XY_RATIO), null);

				g.drawString(u.getType().getDisplayName(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 30);
				g.drawString(u.getPlayer().getDisplayName(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 50);
				g.drawString("Movement: " + u.getType().getMovementDistance(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 70);
				g.drawString("Attackrange: " + u.getType().getMinAttackDistance() + "-" + u.getType().getMaxAttackDistance(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 90);
			}
		}

		if (controller.selectedField != null) {
			Field f = m.getFieldAt(controller.selectedField);

			if (f != Field.VOID) {
				g.drawImage(TextureHandler.getImagePng("field_" + f.toString().toLowerCase()), 400 + lx + 5, 10, (int) (90/ GUIConstants.HEX_TILE_XY_RATIO), 90, null);
				g.drawString("Costs: " + f.getMovementCost(), 400 + lx + 10 + (int) (90/ GUIConstants.HEX_TILE_XY_RATIO), 60);
			}

			g.drawString(String.format("x: %d    y: %d", controller.selectedField.x, controller.selectedField.y), 400 + lx + 10 + (int) (90/ GUIConstants.HEX_TILE_XY_RATIO), 20);
			g.drawString(f.getDisplayName(), 400 + lx + 10 + (int) (90/ GUIConstants.HEX_TILE_XY_RATIO), 40);

			Optional<Unit> unit = m.getUnitAt(controller.selectedField);

			if (unit.isPresent()) {
				Unit u = unit.get();

				g.drawImage(TextureHandler.getImagePng("units_" + u.getType().toString().toLowerCase() + "_" + u.getPlayer().toString().toLowerCase()), 400 + lx + 800/4 + 5, 20, (int) (u.getType().getSize()*90), (int) (u.getType().getSize()*90*GUIConstants.UNIT_XY_RATIO), null);

				g.drawString(u.getType().getDisplayName(), 400 + lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 30);
				g.drawString(u.getPlayer().getDisplayName(), 400 + lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 50);
				g.drawString("Movement: " + u.getType().getMovementDistance(), 400 + lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 70);
				g.drawString("Attackrange: " + u.getType().getMinAttackDistance() + "-" + u.getType().getMaxAttackDistance(), 400 + lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 90);
			}
		}

		g.setColor(Color.BLACK);
		g.drawLine(lx+400, 0, lx+400, 100);

		bottom.getGraphics().drawImage(buffer2, 0, 0, null);
		drawing2 = false;
	}

	@Override
	public void stop() {
		stop = true;
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

		for (UnitType ut: UnitType.values()) {
			for (PlayerColor pc: PlayerColor.values()) {
				TextureHandler.loadImagePng("units_" + ut.toString().toLowerCase() + "_" + pc.toString().toLowerCase(), "units/" + ut.toString().toLowerCase() + "/" + pc.toString().toLowerCase());
			}
		}

		for (PlayerColor pc: PlayerColor.values()) {
			TextureHandler.loadImagePng("bar_" + pc.toString().toLowerCase(), "ui/bar/bar_" + pc.toString().toLowerCase());
		}

		TextureHandler.loadImagePng("button_audioOn", "ui/buttons/audioOn");
		TextureHandler.loadImagePng("button_audioOff", "ui/buttons/audioOff");

		TextureHandler.loadImagePng("button_musicOn", "ui/buttons/musicOn");
		TextureHandler.loadImagePng("button_musicOff", "ui/buttons/musicOff");

		TextureHandler.loadImagePng("button_centerCamera", "ui/buttons/centerCamera");

		TextureHandler.loadImagePng("button_endTurn", "ui/buttons/endTurn");

		AudioHandler.loadMusicWav("EP", "music/EP");
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
}
