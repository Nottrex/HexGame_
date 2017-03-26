package view;

import view.audio.AudioHandler;
import view.audio.AudioPlayer;
import game.*;
import game.enums.Direction;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitType;
import game.map.GameMap;
import game.util.ActionUtil;
import view.components.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import javax.sound.sampled.Clip;
import javax.swing.*;

public class Window extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	//Window stuff
	protected Insets i;
	protected JPanel panel;
	protected JPanel bottom;
	protected JPanel center;

	private ImageButton button_audioOn;
	private ImageButton button_musicOn;
	private ImageButton button_centerCamera;
	private ImageButton button_endTurn;
	private ImageTextLabel topBar;
	private TextLabel fpsLabel;

	private int fps = 0, width, height;
	private Camera cam;
	private boolean stop = false;

	private MouseInputListener mouseListener;
	private KeyInputListener keyListener;

	private Controller controller;
	private AudioPlayer audioPlayer;
	private boolean audioOn = true;
	private boolean musicOn = true;

	public Window() {
		super("HexGame");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		width = getWidth();
		height = getHeight();

		i = getInsets();
		cam = new Camera();

		mouseListener 	= new MouseInputListener(this);
		keyListener 	= new KeyInputListener(this);

		this.addMouseWheelListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		this.addMouseListener(mouseListener);
		this.addKeyListener(keyListener);

		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				int widthDifference = width - getWidth();
				int heightDifference = height - getHeight();

				if(widthDifference == 0 && heightDifference == 0) return;
				cam.tx += (widthDifference * cam.tzoom / 2);
				cam.ty += (heightDifference * cam.tzoom / 2);

				width = getWidth();
				height = getHeight();
			}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentShown(ComponentEvent e) {}

			@Override
			public void componentHidden(ComponentEvent e) {}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			}
		});

		init();
		initComponents();

		centerCamera();

		new Thread(this).start();
	}

	protected void onMouseWheel(double d) {
		double a = 1;
		if (d < 0) {
			a = 1 / GUIConstants.ZOOM;
		}
		if (d > 0) {
			a = GUIConstants.ZOOM;
		}

		cam.tx -= (mouseListener.getMouseX())*cam.tzoom * (a-1);
		cam.ty -= (mouseListener.getMouseY())*cam.tzoom * (a-1);
		cam.tzoom *= a;
	}

	protected void onMouseDrag(int dx, int dy) {
		cam.tx -= (cam.zoom*dx);
		cam.ty -= (cam.zoom*dy);
	}

	protected void onKeyType(int keyCode) {
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

	private BufferedImage mapBuffer;
	private void redrawMap() {
		if (controller == null || controller.game == null) return;

		GameMap m = controller.game.getMap();

		int maxSize = (int) Math.min((long) (GUIConstants.MAX_HEAP_FILL*Runtime.getRuntime().maxMemory()/4), (long)Integer.MAX_VALUE);

		double width = Math.floor(Math.min(Math.sqrt(maxSize / ((m.getWidth() + (int)Math.ceil(m.getHeight()/2.0)) * GUIConstants.HEX_TILE_XY_RATIO * GUIConstants.HEX_TILE_YY_RATIO * (m.getHeight()+1))), GUIConstants.HEX_TILE_WIDTH_MAX));

		double height = width* GUIConstants.HEX_TILE_XY_RATIO;

		mapBuffer = new BufferedImage((int) (width * (m.getWidth() + (int)Math.ceil(m.getHeight()/2.0))), (int) (height * GUIConstants.HEX_TILE_YY_RATIO * (m.getHeight()+1)), BufferedImage.TYPE_INT_ARGB);
		Graphics g = mapBuffer.getGraphics();

		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, (int) (width * (m.getWidth() + (int)Math.ceil(m.getHeight()/2.0))), (int) (height * GUIConstants.HEX_TILE_YY_RATIO * (m.getHeight()+1)));

		for (int x = 0; x < m.getWidth(); x++) {
			for (int y = 0; y < m.getHeight(); y++) {
				if (m.getFieldAt(x, y) == Field.VOID) continue;

				drawHexField(x+(int)Math.ceil(m.getHeight()/2.0), y, g, TextureHandler.getImagePng("field_" + m.getFieldAt(x, y).toString().toLowerCase()), width, height);
			}
		}
	}

	private boolean drawing = false;
	protected void redrawGame() {
		if (center == null || center.getWidth() <= 0 || center.getHeight() <= 0 || drawing || controller == null || controller.game == null || cam == null) return;
		drawing = true;

		BufferedImage buffer = new BufferedImage(center.getWidth(), center.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics g = buffer.getGraphics();

		g.setColor(GUIConstants.COLOR_GAME_BACKGROUND);
		g.fillRect(0, 0, center.getWidth(), center.getHeight());

		cam.update();

		g.setColor(Color.BLUE);
		GameMap m = controller.game.getMap();
		{	//Draw In-game stuff
			g.translate((int) -(cam.x/cam.zoom), (int) -(cam.y/cam.zoom));

			double wx = 1/cam.zoom;
			double wy = wx* GUIConstants.HEX_TILE_XY_RATIO;
			{
				g.drawImage(mapBuffer, (int) (-((int)Math.ceil(m.getHeight()/2.0))*wx), 0, (int) (wx * (m.getWidth() + (int)Math.ceil(m.getHeight()/2.0))), (int) (wy * GUIConstants.HEX_TILE_YY_RATIO * (m.getHeight()+1)), null);
			}

			for (Unit u: m.getUnits()) {
				UnitType ut = u.getType();
				double w = wx*ut.getSize();
				double h = wy*ut.getSize();

				double py = (u.getY())*(GUIConstants.HEX_TILE_YY_RATIO)*wy + (wy-h)/2;
				double px = (u.getX())*wx - (u.getY())*wy/(2* GUIConstants.HEX_TILE_XY_RATIO) + (wx-w)/2;

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
						List<Direction> movements = controller.pa.moveTo(mloc);
						Location a = selectedField;

						for (Direction d: movements) {
							drawMovementArrow(a.x, a.y, g, d, wx, wy);
							a = d.applyMovement(a);
						}
					} else if (controller.pa.canAttack().contains(mloc)) {
						List<Direction> movements = controller.pa.moveToToAttack(mloc);
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

		center.getGraphics().drawImage(buffer, 0, 0, null);
		drawing = false;
	}

	protected void onMouseClick(int x, int y) {
		controller.onMouseClick(getHexFieldPosition(x, y));
	}

	private void drawHexField(int x, int y, Graphics g, BufferedImage img, double wx, double wy) {
		double py = (y)*(GUIConstants.HEX_TILE_YY_RATIO)*wy;
		double px = (x)*wx - (y)*wy/(2* GUIConstants.HEX_TILE_XY_RATIO);
		g.drawImage(img, (int) px, (int) py, (int) wx +2, (int) wy +2, null);
	}

	private void drawMovementArrow(int x, int y, Graphics g, Direction d, double wx, double wy) {
			double centerY1 = (y)*(GUIConstants.HEX_TILE_YY_RATIO)*wy + wy/2;
			double centerX1 = (x)*wx - (y)*wy/(2* GUIConstants.HEX_TILE_XY_RATIO) + wx/2;

			double centerY2 = (y+d.getYMovement())*(GUIConstants.HEX_TILE_YY_RATIO)*wy + wy/2;
			double centerX2 = (x+d.getXMovement())*wx - (y+d.getYMovement())*wy/(2* GUIConstants.HEX_TILE_XY_RATIO) + wx/2;

			switch (d) {
			case RIGHT:
				g.drawImage(TextureHandler.getImagePng("arrow_right"), (int) (centerX1+wx*GUIConstants.ARROW_SIZE/2), (int) (centerY2 - wx*GUIConstants.ARROW_SIZE/2), (int) (wx*GUIConstants.ARROW_SIZE), (int) (GUIConstants.ARROW_SIZE*wx), null);
				break;
			case LEFT:
				g.drawImage(TextureHandler.getImagePng("arrow_left"), (int) (centerX2+wx*GUIConstants.ARROW_SIZE/2), (int) (centerY2 - wx*GUIConstants.ARROW_SIZE/2), (int) (wx*GUIConstants.ARROW_SIZE), (int) (GUIConstants.ARROW_SIZE*wx), null);
				break;
			case UP_RIGHT:
				g.drawImage(TextureHandler.getImagePng("arrow_up_right"), (int) (centerX1+((centerX2-centerX1)-wx*GUIConstants.ARROW_SIZE)/2), (int) (centerY1 + ((centerY2-centerY1) - wx*GUIConstants.ARROW_SIZE)/2), (int) (wx*GUIConstants.ARROW_SIZE), (int) (wx*GUIConstants.ARROW_SIZE), null);
				break;
			case UP_LEFT:
				g.drawImage(TextureHandler.getImagePng("arrow_up_left"), (int) (centerX1+((centerX2-centerX1)-wx*GUIConstants.ARROW_SIZE)/2), (int) (centerY1 + ((centerY2-centerY1) - wx*GUIConstants.ARROW_SIZE)/2), (int) (wx*GUIConstants.ARROW_SIZE), (int) (wx*GUIConstants.ARROW_SIZE), null);
				break;
			case DOWN_LEFT:
				g.drawImage(TextureHandler.getImagePng("arrow_down_left"), (int) (centerX1+((centerX2-centerX1)-wx*GUIConstants.ARROW_SIZE)/2), (int) (centerY1 + ((centerY2-centerY1) - wx*GUIConstants.ARROW_SIZE)/2), (int) (wx*GUIConstants.ARROW_SIZE), (int) (wx*GUIConstants.ARROW_SIZE), null);
				break;
			case DOWN_RIGHT:
				g.drawImage(TextureHandler.getImagePng("arrow_down_right"), (int) (centerX1+((centerX2-centerX1)-wx*GUIConstants.ARROW_SIZE)/2), (int) (centerY1 + ((centerY2-centerY1) - wx*GUIConstants.ARROW_SIZE)/2), (int) (wx*GUIConstants.ARROW_SIZE), (int) (wx*GUIConstants.ARROW_SIZE), null);
				break;
			default:
				break;
		}
	}

	private boolean drawing2 = false;
	protected void redrawInfoBar() {
		if (bottom == null || bottom.getWidth() <= 0 || bottom.getHeight() <= 0 || controller == null || controller.game == null || drawing2) return;
		drawing2 = true;

		BufferedImage buffer2 = new BufferedImage(bottom.getWidth(), bottom.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		GameMap m = controller.game.getMap();

		Graphics g = buffer2.getGraphics();
		g.setColor(GUIConstants.COLOR_INFOBAR_BACKGROUND);
		g.fillRect(0, 0, bottom.getWidth(), bottom.getHeight());


		g.setColor(Color.WHITE);
		int lx = (bottom.getWidth()-800)/2;

		Location mouseLocation = getHexFieldPosition(mouseListener.getMouseX(), mouseListener.getMouseY());

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

				g.drawImage(TextureHandler.getImagePng("units_" + u.getType().toString().toLowerCase() + "_" + u.getPlayer().toString().toLowerCase()), lx + 800/4 + 5, 20, (int) (u.getType().getSize()*90), (int) (u.getType().getSize()*90), null);

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

				g.drawImage(TextureHandler.getImagePng("units_" + u.getType().toString().toLowerCase() + "_" + u.getPlayer().toString().toLowerCase()), 400 + lx + 800/4 + 5, 20, (int) (u.getType().getSize()*90), (int) (u.getType().getSize()*90), null);

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

	private void centerCamera() {
		GameMap m = controller.game.getMap();

		cam.tzoom = (m.getHeight()* GUIConstants.HEX_TILE_XY_RATIO* GUIConstants.HEX_TILE_XY_RATIO) / center.getHeight();

		cam.ty = (GUIConstants.HEX_TILE_XY_RATIO)/2-cam.tzoom*center.getHeight()/2 + (m.getHeight()/2)* GUIConstants.HEX_TILE_XY_RATIO* GUIConstants.HEX_TILE_YY_RATIO - 20*cam.tzoom;
		cam.tx = 0.5 - cam.tzoom*center.getWidth()/2 + (m.getWidth()/2) - (m.getHeight()/4);
	}

	private Location getHexFieldPosition(int px, int py) {
		double dy = (py + cam.y/cam.zoom) / ((GUIConstants.HEX_TILE_YY_RATIO)* GUIConstants.HEX_TILE_XY_RATIO/cam.zoom);

		int	y = (int) Math.floor(dy);
		int	x = (int) Math.floor((px + cam.x/cam.zoom + (y)*(GUIConstants.HEX_TILE_XY_RATIO/cam.zoom)/(2* GUIConstants.HEX_TILE_XY_RATIO)) * cam.zoom);

		if ((dy%1) <= (1- GUIConstants.HEX_TILE_YY_RATIO) / (GUIConstants.HEX_TILE_YY_RATIO)) {
			int my = (int) Math.floor(dy);
			double vx = ((px + cam.x/cam.zoom + (my)*(GUIConstants.HEX_TILE_XY_RATIO/cam.zoom)/(2* GUIConstants.HEX_TILE_XY_RATIO)) * cam.zoom ) % 1;
			double vy = ((dy%1) / ((1- GUIConstants.HEX_TILE_YY_RATIO) / (GUIConstants.HEX_TILE_YY_RATIO)))/2;

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

	@Override
	public void run() {
		int i = 0;
		long t = System.currentTimeMillis();
		while (!stop) {
			i++;
			redrawGame();
			audioPlayer.updateVolume();

			if (System.currentTimeMillis()-t > 500) {
				long t2 = System.currentTimeMillis();
				fps = (int) (i / ((t2-t)/1000.0));
				t = t2;
				i = 0;
			}
		}
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		this.setContentPane(panel);

		bottom = new JPanel(null) {
			@Override
			public void repaint() {
				redrawInfoBar();
			}
		};
		bottom.setPreferredSize(new Dimension(800, 100));
		bottom.setBackground(GUIConstants.COLOR_INFOBAR_BACKGROUND);

		center = new JPanel(null) {
			@Override
			public void repaint() {
				redrawGame();
			}
		};
		center.setBackground(GUIConstants.COLOR_GAME_BACKGROUND);
		button_audioOn = new ImageButton(TextureHandler.getImagePng("button_audioOn"), e -> onKeyType(KeyBindings.KEY_TOGGLE_AUDIO));
		button_musicOn = new ImageButton(TextureHandler.getImagePng("button_musicOn"), e -> onKeyType(KeyBindings.KEY_TOGGLE_MUSIC));
		button_centerCamera = new ImageButton(TextureHandler.getImagePng("button_centerCamera"), e -> onKeyType(KeyBindings.KEY_CENTER_CAMERA));
		button_endTurn = new ImageButton(TextureHandler.getImagePng("button_endTurn"), e -> onKeyType(KeyBindings.KEY_NEXT_PLAYER));
		fpsLabel = new TextLabel(() -> ("FPS: " + fps));
		topBar = new ImageTextLabel(new ImageTextLabel.ImageText() {
			@Override
			public BufferedImage getImage() {
				return TextureHandler.getImagePng("bar_" + controller.game.getPlayerTurn().toString().toLowerCase());
			}

			@Override
			public String getText() {
				return String.format("Round %d   %d / %d   %s", controller.game.getRound(), controller.game.getPlayerTurnID(), controller.game.getPlayerAmount(), controller.game.getPlayerTurn().getDisplayName());
			}
		});

		center.add(button_audioOn);
		center.add(button_musicOn);
		center.add(button_centerCamera);
		center.add(button_endTurn);
		center.add(topBar);
		center.add(fpsLabel);
		center.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int height = (center.getHeight() + center.getWidth()) / 2;
				int width = center.getWidth();
				int buttonHeight = height / 30;
				int barHeight = height / 30;

				button_audioOn.setBounds(width - buttonHeight - 5, 5, buttonHeight, buttonHeight);
				button_musicOn.setBounds(width - buttonHeight*2 - 5*2, 5, buttonHeight, buttonHeight);
				button_centerCamera.setBounds(width - buttonHeight*3 - 5*3, 5, buttonHeight, buttonHeight);
				topBar.setBounds((width-(380*barHeight)/49)/2, 5, (380*barHeight)/49, barHeight);
				fpsLabel.setBounds(5, 5, barHeight*5, barHeight);


				if (controller != null && controller.localPlayers.contains(controller.game.getPlayerTurn())) {
					button_endTurn.setBounds(width - buttonHeight - 5, center.getHeight() - buttonHeight - 5, buttonHeight, buttonHeight);
				}
			}
		});

		panel.add(center, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.PAGE_END);
		panel.updateUI();

		audioPlayer = new AudioPlayer("EP", Clip.LOOP_CONTINUOUSLY);
		audioPlayer.start();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		TextureHandler.loadImagePng("cursor","ui/cursor");
		Cursor c = toolkit.createCustomCursor(TextureHandler.getImagePng("cursor") , new Point(0, 0), "img");
		this.setCursor(c);
	}

	private void init() {
		controller = new Controller(this);

		for (Field f: Field.values()) {
			if (f != Field.VOID) {
				TextureHandler.loadImagePng("field_" + f.toString().toLowerCase(), "field/" + f.toString().toLowerCase());
			}
		}

		for (UnitType ut: UnitType.values()) {
			for (PlayerColor pc: PlayerColor.values()) {
				TextureHandler.loadImagePng("units_" + ut.toString().toLowerCase() + "_" + pc.toString().toLowerCase(), "units/" + ut.toString().toLowerCase() + "/" + pc.toString().toLowerCase());
			}
		}

		for (PlayerColor pc: PlayerColor.values()) {
			TextureHandler.loadImagePng("bar_" + pc.toString().toLowerCase(), "ui/bar/bar_" + pc.toString().toLowerCase());
		}

		for (Direction d: Direction.values()) {
			TextureHandler.loadImagePng("arrow_" + d.toString().toLowerCase(), "arrow/arrow_" + d.toString().toLowerCase());
		}

		TextureHandler.loadImagePng("fieldmarker_select", "fieldmarker/select");
		TextureHandler.loadImagePng("fieldmarker_select2", "fieldmarker/overlay/normalVersions/normalYellow");
		TextureHandler.loadImagePng("fieldmarker_red", "fieldmarker/overlay/normalVersions/normalRed");
		TextureHandler.loadImagePng("fieldmarker_blue", "fieldmarker/overlay/normalVersions/normalBlue");

		TextureHandler.loadImagePng("button_audioOn", "ui/buttons/audioOn");
		TextureHandler.loadImagePng("button_audioOff", "ui/buttons/audioOff");

		TextureHandler.loadImagePng("button_musicOn", "ui/buttons/musicOn");
		TextureHandler.loadImagePng("button_musicOff", "ui/buttons/musicOff");

		TextureHandler.loadImagePng("button_centerCamera", "ui/buttons/centerCamera");

		TextureHandler.loadImagePng("button_endTurn", "ui/buttons/endTurn");

		AudioHandler.loadMusicWav("EP", "music/EP");
		redrawMap();
	}
}
