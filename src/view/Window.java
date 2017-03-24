package view;

import game.*;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitType;
import game.map.GameMap;
import game.util.ActionUtil;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

import javax.swing.*;

public class Window extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	//Window stuff
	protected Insets i;
	protected JPanel panel;
	protected JPanel bottom;
	protected JPanel center;

	private Camera cam;
	private boolean stop = false;

	private MouseInputListener mouseListener;
	private KeyInputListener keyListener;

	private Controller controller;

	public Window() {
		super("HexGame");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		initComponents();

		i = getInsets();
		cam = new Camera();

		mouseListener 	= new MouseInputListener(this);
		keyListener 	= new KeyInputListener(this);

		this.addMouseWheelListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		this.addMouseListener(mouseListener);
		this.addKeyListener(keyListener);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

			}
		});

		init();

		new Thread(this).start();
	}

	private void init() {
		controller = new Controller(this);

		TextureHandler.loadImagePng("fieldmarker_select", "fieldmarker/select");
		TextureHandler.loadImagePng("fieldmarker_select2", "fieldmarker/overlay/normalVersions/normalYellow");
		TextureHandler.loadImagePng("fieldmarker_red", "fieldmarker/overlay/normalVersions/normalRed");
		TextureHandler.loadImagePng("fieldmarker_blue", "fieldmarker/overlay/normalVersions/normalBlue");

		for (PlayerColor pc: PlayerColor.values()) {
			TextureHandler.loadImagePng("bar_" + pc.getTextureName(), "ui/bar/bar_" + pc.getTextureName());
		}

		centerCamera();
	}

	protected void onMouseWheel(double d) {
		double a = 1;
		if (d < 0) {
			a = 1 / Constants.ZOOM;
		}
		if (d > 0) {
			a = Constants.ZOOM;
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
		if (keyCode == KeyEvent.VK_C) {
			centerCamera();
		}

		controller.onKeyType(keyCode);
	}

	private boolean drawing = false;
	protected void redrawGame() {
		if (center == null || center.getWidth() <= 0 || center.getHeight() <= 0 || drawing) return;
		drawing = true;


		BufferedImage buffer = new BufferedImage(center.getWidth(), center.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

		Graphics g = buffer.getGraphics();

		g.setColor(Constants.COLOR_GAME_BACKGROUND);
		g.fillRect(0, 0, center.getWidth(), center.getHeight());

		if (controller == null || controller.game == null || cam == null) {
			drawing = false;
			return;
		}

		cam.update();

		g.setColor(Color.BLUE);
		GameMap m = controller.game.getMap();

		g.translate((int) -(cam.x/cam.zoom), (int) -(cam.y/cam.zoom));

		double wx = 1/cam.zoom;
		double wy = wx*Constants.HEX_TILE_XY_RATIO;
		for (int x = 0; x < m.getWidth(); x++) {
			for (int y = 0; y < m.getHeight(); y++) {
				if (m.getFieldAt(x, y).getTextureName() == null) continue;

				drawHexField(x, y, g, TextureHandler.getImagePng("field_" + m.getFieldAt(x, y).getTextureName()), wx, wy);
			}
		}

		for (Unit u: m.getUnits()) {
			UnitType ut = u.getType();
			double w = wx*ut.getSize();
			double h = wy*ut.getSize();

			double py = (u.getY())*(Constants.HEX_TILE_YY_RATIO)*wy + (wy-h)/2;
			double px = (u.getX())*wx - (u.getY())*wy/(2*Constants.HEX_TILE_XY_RATIO) + (wx-w)/2;

			g.drawImage(TextureHandler.getImagePng("units_" + ut.getTextureName() + "_" + u.getPlayer().getTextureName()), (int) px, (int) py, (int) w, (int) h, null);
		}

		Location mloc = getHexFieldPosition(mouseListener.getMouseX(), mouseListener.getMouseY());
		drawHexField(mloc.x, mloc.y, g, TextureHandler.getImagePng("fieldmarker_select"), wx, wy);

		if (controller.selectedField != null) {
			drawHexField(controller.selectedField.x, controller.selectedField.y, g, TextureHandler.getImagePng("fieldmarker_select2"), wx, wy);

			Optional<Unit> u = m.getUnitAt(controller.selectedField);

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
			}
		}

		g.translate((int) (cam.x/cam.zoom), (int) (cam.y/cam.zoom));


		{	//Draw TopBar
			int width = 350;
			int height = 49;
			int x = (center.getWidth()-width)/2;

			g.drawImage(TextureHandler.getImagePng("bar_" + controller.game.getPlayerTurn().getTextureName()), x, 0, width, height, null);
		}

		center.getGraphics().drawImage(buffer, 0, 0, null);
		drawing = false;
	}

	protected void onMouseClick(int x, int y) {
		controller.onMouseClick(getHexFieldPosition(x, y));
	}

	private void drawHexField(int x, int y, Graphics g, BufferedImage img, double wx, double wy) {
		double py = (y)*(Constants.HEX_TILE_YY_RATIO)*wy;
		double px = (x)*wx - (y)*wy/(2*Constants.HEX_TILE_XY_RATIO);
		g.drawImage(img, (int) px, (int) py, (int) wx +2, (int) wy +2, null);
	}

	private boolean drawing2 = false;
	protected void redrawInfoBar() {
		if (bottom == null || bottom.getWidth() <= 0 || bottom.getHeight() <= 0 || controller == null || controller.game == null || drawing2) return;
		drawing2 = true;

		BufferedImage buffer2 = new BufferedImage(bottom.getWidth(), bottom.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		GameMap m = controller.game.getMap();

		Graphics g = buffer2.getGraphics();
		g.setColor(Constants.COLOR_INFOBAR_BACKGROUND);
		g.fillRect(0, 0, bottom.getWidth(), bottom.getHeight());


		g.setColor(Color.WHITE);
		int lx = (bottom.getWidth()-800)/2;

		Location mouseLocation = getHexFieldPosition(mouseListener.getMouseX(), mouseListener.getMouseY());

		if (mouseLocation != null) {
			Field f = m.getFieldAt(mouseLocation);

			if (f != Field.VOID) {
				g.drawImage(TextureHandler.getImagePng("field_" + f.getTextureName()), lx + 5, 10, (int) (90/Constants.HEX_TILE_XY_RATIO), 90, null);
				g.drawString("Costs: " + f.getMovementCost(), lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 60);
			}

			g.drawString(String.format("x: %d    y: %d", mouseLocation.x, mouseLocation.y), lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 20);
			g.drawString(f.getDisplayName(), lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 40);

			Optional<Unit> unit = m.getUnitAt(mouseLocation);

			if (unit.isPresent()) {
				Unit u = unit.get();

				g.drawImage(TextureHandler.getImagePng("units_" + u.getType().getTextureName() + "_" + u.getPlayer().getTextureName()), lx + 800/4 + 5, 20, (int) (u.getType().getSize()*90), (int) (u.getType().getSize()*90), null);

				g.drawString(u.getType().getDisplayName(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 30);
				g.drawString(u.getPlayer().getDisplayName(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 50);
				g.drawString("Movement: " + u.getType().getMovementDistance(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 70);
				g.drawString("Attackrange: " + u.getType().getMinAttackDistance() + "-" + u.getType().getMaxAttackDistance(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 90);
			}
		}

		if (controller.selectedField != null) {
			Field f = m.getFieldAt(controller.selectedField);

			if (f != Field.VOID) {
				g.drawImage(TextureHandler.getImagePng("field_" + f.getTextureName()), 400 + lx + 5, 10, (int) (90/Constants.HEX_TILE_XY_RATIO), 90, null);
                g.drawString("Costs: " + f.getMovementCost(), 400 + lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 60);
			}

			g.drawString(String.format("x: %d    y: %d", controller.selectedField.x, controller.selectedField.y), 400 + lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 20);
			g.drawString(f.getDisplayName(), 400 + lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 40);

			Optional<Unit> unit = m.getUnitAt(controller.selectedField);

			if (unit.isPresent()) {
				Unit u = unit.get();

				g.drawImage(TextureHandler.getImagePng("units_" + u.getType().getTextureName() + "_" + u.getPlayer().getTextureName()), 400 + lx + 800/4 + 5, 20, (int) (u.getType().getSize()*90), (int) (u.getType().getSize()*90), null);

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

		cam.tzoom = (m.getHeight()*Constants.HEX_TILE_XY_RATIO*Constants.HEX_TILE_XY_RATIO) / center.getHeight();

		cam.ty = (Constants.HEX_TILE_XY_RATIO)/2-cam.tzoom*center.getHeight()/2 + (m.getHeight()/2)*Constants.HEX_TILE_XY_RATIO*Constants.HEX_TILE_YY_RATIO;
		cam.tx = 0.5 - cam.tzoom*center.getWidth()/2 + (m.getWidth()/2) - (m.getHeight()/4);
	}

	private Location getHexFieldPosition(int px, int py) {
		double dy = (py + cam.y/cam.zoom) / ((Constants.HEX_TILE_YY_RATIO)*Constants.HEX_TILE_XY_RATIO/cam.zoom);

		int	y = (int) Math.floor(dy);
		int	x = (int) Math.floor((px + cam.x/cam.zoom + (y)*(Constants.HEX_TILE_XY_RATIO/cam.zoom)/(2*Constants.HEX_TILE_XY_RATIO)) * cam.zoom);

		if ((dy%1) <= (1-Constants.HEX_TILE_YY_RATIO) / (Constants.HEX_TILE_YY_RATIO)) {
			int my = (int) Math.floor(dy);
			double vx = ((px + cam.x/cam.zoom + (my)*(Constants.HEX_TILE_XY_RATIO/cam.zoom)/(2*Constants.HEX_TILE_XY_RATIO)) * cam.zoom ) % 1;
			double vy = ((dy%1) / ((1-Constants.HEX_TILE_YY_RATIO) / (Constants.HEX_TILE_YY_RATIO)))/2;

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
		while (!stop) {
			redrawGame();
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
		bottom.setBackground(Constants.COLOR_INFOBAR_BACKGROUND);

		center = new JPanel(null) {
			@Override
			public void repaint() {
				redrawGame();
			}
		};
		center.setBackground(Constants.COLOR_GAME_BACKGROUND);

		panel.add(center, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.PAGE_END);


		Toolkit toolkit = Toolkit.getDefaultToolkit();
		TextureHandler.loadImagePng("cursor","ui/cursor");
		Cursor c = toolkit.createCustomCursor(TextureHandler.getImagePng("cursor") , new Point(0, 0), "img");
		this.setCursor (c);
	}
}
