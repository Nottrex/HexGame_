package view;

import game.*;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitType;
import game.util.ActionUtil;
import game.util.PossibleActions;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.swing.*;

public class Window extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	private Insets i;
	private Camera cam;

	private JPanel panel;
	private JPanel top;
		private JLabel top_info;
	private JPanel bottom;
	private JPanel center;

	private boolean mousePressedInGame = false;
	private int lastX = 0, lastY = 0;
	private int mouseX = 0, mouseY = 0;

	private boolean stop = false;
	//Gamestuff
	private Game game;
	private List<PlayerColor> localPlayers;
	private Location selecetedField = null;
	private PossibleActions pa = null;

	public Window() {
		super("HexGame");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		panel = new JPanel(new BorderLayout());
		this.setContentPane(panel);

		top = new JPanel(new BorderLayout());
		top.setBackground(Constants.COLOR_TOPBAR_BACKRGOUND);
		top.setPreferredSize(new Dimension(500, 25));

		top_info = new JLabel("Test", SwingConstants.CENTER);
		top.add(top_info, BorderLayout.CENTER);

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

		panel.add(top, BorderLayout.PAGE_START);
		panel.add(center, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.PAGE_END);


		i = getInsets();
		cam = new Camera();

		this.addMouseWheelListener(e -> onMouseWheel(e.getScrollAmount() * e.getPreciseWheelRotation()));

		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (mousePressedInGame) {
					onMouseMove(e.getX() - lastX, e.getY() - lastY);
					lastX = e.getX();
					lastY = e.getY();
				}
				int x = e.getX() - i.left;
				int y = e.getY() - i.top;
				if (x >= center.getX() && x < (center.getX() + center.getWidth()) && y >= center.getY() && y < (center.getY() + center.getHeight())) {
					mouseX = x-center.getX();
					mouseY = y-center.getY();
					redrawInfoBar();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX() - i.left;
				int y = e.getY() - i.top;
				if (x >= center.getX() && x < (center.getX() + center.getWidth()) && y >= center.getY() && y < (center.getY() + center.getHeight())) {
					mouseX = x-center.getX();
					mouseY = y-center.getY();
					redrawInfoBar();
				}
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX() - i.left;
				int y = e.getY() - i.top;
				if (x >= center.getX() && x < (center.getX() + center.getWidth()) && y >= center.getY() && y < (center.getY() + center.getHeight())) {
					onMouseClick(x - center.getX(), y - center.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX() - i.left;
				int y = e.getY() - i.top;
				if (x >= center.getX() && x < (center.getX() + center.getWidth()) && y >= center.getY() && y < (center.getY() + center.getHeight())) {
					mousePressedInGame = true;
					lastX = e.getX();
					lastY = e.getY();
				}
			}
		});

		this.addKeyListener(new KeyAdapter() {

			HashMap<Integer, Boolean> pressed = new HashMap<>();

			private boolean isPressed(int i) {
				return pressed.containsKey(i) ? pressed.get(i) : false;
			}

			@Override
			public void keyReleased(KeyEvent e) {
				pressed.put(e.getKeyCode(), false);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (!isPressed(e.getKeyCode())) {
					onKeyType(e.getKeyCode());
				}

				pressed.put(e.getKeyCode(), true);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

			}
		});

		init();

		new Thread(this).start();
	}

	private void init() {
		game = new Game();
		localPlayers = new ArrayList<>();
		localPlayers.add(PlayerColor.BLUE);
		localPlayers.add(PlayerColor.RED);

		TextureHandler.loadImagePng("fieldmarker_select", "fieldmarker/select");
		TextureHandler.loadImagePng("fieldmarker_select2", "fieldmarker/select2");

		centerCamera();
		redrawTopBar();
	}

	private void onMouseWheel(double d) {
		double a = 1;
		if (d < 0) {
			a = 1/Constants.ZOOM;
		}
		if (d > 0) {
			a = Constants.ZOOM;
		}

		cam.tzoom *= a;
		cam.tx *= a;
		cam.ty *= a;
	}

	private void onMouseClick(int x, int y) {
		Location l = getHexFieldPosition(x, y);
		if (selecetedField == null) {
			if (game.getMap().getFieldAt(l) != Field.VOID)
				selecetedField = l;
		} else {


			if (game.getMap().getFieldAt(l) == Field.VOID) {
				selecetedField = null;
			} else {
				Optional<Unit> u = game.getUnitAt(selecetedField);
				Optional<Unit> u2 = game.getUnitAt(l);

				if (u.isPresent() && !u2.isPresent()) {
					game.moveUnitTo(u.get(), l.x, l.y);

					selecetedField = null;
				} else {
					selecetedField = l;
				}
			}
		}

		if (selecetedField != null) {
			Optional<Unit> u = game.getUnitAt(selecetedField);
			if (u.isPresent()) pa = ActionUtil.getPossibleActions(game, u.get());
		}

		redrawInfoBar();
	}

	private void onMouseMove(int dx, int dy) {
		cam.tx -= (cam.zoom*dx);
		cam.ty -= (cam.zoom*dy);
	}

	private void onKeyType(int keyCode) {
		if (keyCode == KeyEvent.VK_C) {
			centerCamera();
		}
	}

	private void redrawTopBar() {
		top_info.setText("Round: " + game.getRound() + "   " + game.getPlayerTurn() + "   " + game.getPlayerTurnID() + " / " + game.getPlayerAmount());
	}

	private boolean drawing = false;
	private void redrawGame() {
		if (center == null || center.getWidth() <= 0 || center.getHeight() <= 0 || game == null || drawing) return;
		drawing = true;

		cam.update();

		BufferedImage buffer = new BufferedImage(center.getWidth(), center.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

		Graphics g = buffer.getGraphics();

		g.setColor(Constants.COLOR_GAME_BACKGROUND);
		g.fillRect(0, 0, center.getWidth(), center.getHeight());

		g.setColor(Color.BLUE);
		GameMap m = game.getMap();

		g.translate((int) -(cam.x/cam.zoom), (int) -(cam.y/cam.zoom));

		double wx = 1/cam.zoom;
		double wy = wx*Constants.HEX_TILE_XY_RATIO;
		for (int x = 0; x < m.getWidth(); x++) {
			for (int y = 0; y < m.getHeight(); y++) {
				if (m.getFieldAt(x, y).getTextureName() == null) continue;

				drawHexField(x - m.getWidth()/2, y - m.getHeight()/2, g, TextureHandler.getImagePng("field_" + m.getFieldAt(x, y).getTextureName()), wx, wy);
			}
		}

		for (Unit u: game.getUnits()) {
			UnitType ut = u.getType();
			double w = wx*ut.getSize();
			double h = wy*ut.getSize();

			double py = (u.getY() - m.getHeight()/2)*(Constants.HEX_TILE_YY_RATIO)*wy + (wy-h)/2;
			double px = (u.getX() - m.getWidth()/2)*wx - (u.getY() - m.getHeight()/2)*wy/(2*Constants.HEX_TILE_XY_RATIO) + (wx-w)/2;

			g.drawImage(TextureHandler.getImagePng("units_" + ut.getTextureName() + "_" + u.getPlayer().getTextureName()), (int) px, (int) py, (int) w, (int) h, null);
		}

		Location mloc = getHexFieldPosition(mouseX, mouseY);
		drawHexField(mloc.x - m.getWidth()/2, mloc.y - m.getHeight()/2, g, TextureHandler.getImagePng("fieldmarker_select"), wx, wy);

		if (selecetedField != null) {
			drawHexField(selecetedField.x - m.getWidth()/2, selecetedField.y - m.getHeight()/2, g, TextureHandler.getImagePng("fieldmarker_select2"), wx, wy);

			Optional<Unit> u = game.getUnitAt(selecetedField);

			if (u.isPresent()) {

				if (pa == null) {
					pa = ActionUtil.getPossibleActions(game, u.get());
				}

				for (Location target: pa.canMoveTo()) {
					drawHexField(target.x - m.getWidth()/2, target.y - m.getHeight()/2, g, TextureHandler.getImagePng("fieldmarker_select2"), wx, wy);
				}
			}
		}

		g.translate((int) (cam.x/cam.zoom), (int) (cam.y/cam.zoom));
		center.getGraphics().drawImage(buffer, 0, 0, null);
		drawing = false;
	}

	private void drawHexField(int x, int y, Graphics g, BufferedImage img, double wx, double wy) {
		double py = (y)*(Constants.HEX_TILE_YY_RATIO)*wy;
		double px = (x)*wx - (y)*wy/(2*Constants.HEX_TILE_XY_RATIO);
		g.drawImage(img, (int) px, (int) py, (int) wx +2, (int) wy +2, null);
	}

	private boolean drawing2 = false;
	private void redrawInfoBar() {
		if (bottom == null || bottom.getWidth() <= 0 || bottom.getHeight() <= 0 || game == null || drawing2) return;
		drawing2 = true;

		BufferedImage buffer2 = new BufferedImage(bottom.getWidth(), bottom.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		GameMap m = game.getMap();

		Graphics g = buffer2.getGraphics();
		g.setColor(Constants.COLOR_INFOBAR_BACKGROUND);
		g.fillRect(0, 0, bottom.getWidth(), bottom.getHeight());


		g.setColor(Color.WHITE);
		int lx = (bottom.getWidth()-800)/2;

		Location mouseLocation = getHexFieldPosition(mouseX, mouseY);

		if (mouseLocation != null) {
			Field f = m.getFieldAt(mouseLocation);

			if (f != Field.VOID) {
				g.drawImage(TextureHandler.getImagePng("field_" + f.getTextureName()), lx + 5, 10, (int) (90/Constants.HEX_TILE_XY_RATIO), 90, null);
			}

			g.drawString(String.format("x: %d    y: %d", mouseLocation.x, mouseLocation.y), lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 20);
			g.drawString(f.getDisplayName(), lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 50);

			Optional<Unit> unit = game.getUnitAt(mouseLocation);

			if (unit.isPresent()) {
				Unit u = unit.get();

				g.drawImage(TextureHandler.getImagePng("units_" + u.getType().getTextureName() + "_" + u.getPlayer().getTextureName()), lx + 800/4 + 5, 20, (int) (u.getType().getSize()*90), (int) (u.getType().getSize()*90), null);

				g.drawString(u.getType().getDisplayName(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 30);
				g.drawString(u.getPlayer().getDisplayName(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 50);
				g.drawString("Movement: " + u.getType().getMovementDistance(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 70);
				g.drawString("Attackrange: " + u.getType().getMinAttackDistance() + "-" + u.getType().getMaxAttackDistance(), lx + 800/4 + 5 + (int) (u.getType().getSize()*90) + 10, 90);
			}
		}

		if (selecetedField != null) {
			Field f = m.getFieldAt(selecetedField);

			if (f != Field.VOID) {
				g.drawImage(TextureHandler.getImagePng("field_" + f.getTextureName()), 400 + lx + 5, 10, (int) (90/Constants.HEX_TILE_XY_RATIO), 90, null);
			}

			g.drawString(String.format("x: %d    y: %d", selecetedField.x, selecetedField.y), 400 + lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 20);
			g.drawString(f.getDisplayName(), 400 + lx + 10 + (int) (90/Constants.HEX_TILE_XY_RATIO), 50);

			Optional<Unit> unit = game.getUnitAt(selecetedField);

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
		GameMap m = game.getMap();

		cam.tzoom = (m.getHeight()*Constants.HEX_TILE_XY_RATIO*Constants.HEX_TILE_XY_RATIO) / center.getHeight();

		cam.ty = (Constants.HEX_TILE_XY_RATIO)/2-cam.tzoom*center.getHeight()/2;
		cam.tx = 0.5 - cam.tzoom*center.getWidth()/2;
	}

	private Location getHexFieldPosition(int px, int py) {

		GameMap m = game.getMap();

		double dy = (py + cam.y/cam.zoom) / ((Constants.HEX_TILE_YY_RATIO)*Constants.HEX_TILE_XY_RATIO/cam.zoom) + m.getHeight()/2;
		int y = (int) Math.floor(dy);
		int x = (int) Math.floor((px + cam.x/cam.zoom + (y - m.getHeight()/2)*(Constants.HEX_TILE_XY_RATIO/cam.zoom)/(2*Constants.HEX_TILE_XY_RATIO)) * cam.zoom + m.getWidth()/2);

		if ((dy%1) <= (1-Constants.HEX_TILE_YY_RATIO) / (Constants.HEX_TILE_YY_RATIO)) {
			int my = (int) Math.floor(dy);
			double vx = ((px + cam.x/cam.zoom + (my - m.getHeight()/2)*(Constants.HEX_TILE_XY_RATIO/cam.zoom)/(2*Constants.HEX_TILE_XY_RATIO)) * cam.zoom + m.getWidth()/2) % 1;
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
}
