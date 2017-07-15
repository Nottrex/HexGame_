package client.game.overlay;

import client.game.Controller;
import client.game.ViewGame;
import client.game.gameView.GameView;
import client.i18n.LanguageHandler;
import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.Window;
import client.window.components.ImageButton;
import game.Location;
import game.Unit;
import game.enums.UnitState;
import game.enums.UnitType;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UnitSpawnOverlay extends Overlay {

	private ImageButton spawnTank;
	private ImageButton spawnArtillerie;
	private ImageButton spawnKavallerie;
	private ImageButton spawnInfanterie;

	private UnitType selType;

	public UnitSpawnOverlay(Window w, ViewGame game, Controller c, int x, int y) {
		float[] a = ((GameView) game.getCenter()).screenPositionToWorldPosition(x, y);
		Location b = ((GameView) game.getCenter()).getHexFieldPosition(a[0], a[1]);

		selType = null;

		setBounds(x, y, 120 + 20 + 100, 140 + 15);
		spawnTank = new ImageButton(w, TextureHandler.getImagePng("unit_" + c.game.getPlayerColor().toString().toLowerCase() + "_panzer"), e -> {
			game.setOverlay(null);
			c.spawnUnit(new Unit(c.game.getPlayerColor(), UnitType.PANZER, 1, UnitState.INACTIVE, b.x, b.y));
		});
		spawnArtillerie = new ImageButton(w, TextureHandler.getImagePng("unit_" + c.game.getPlayerColor().toString().toLowerCase() + "_artillerie"), e -> {
			game.setOverlay(null);
			c.spawnUnit(new Unit(c.game.getPlayerColor(), UnitType.ARTILLERIE, 1, UnitState.INACTIVE, b.x, b.y));
		});
		spawnKavallerie = new ImageButton(w, TextureHandler.getImagePng("unit_" + c.game.getPlayerColor().toString().toLowerCase() + "_kavallerie"), e -> {
			game.setOverlay(null);
			c.spawnUnit(new Unit(c.game.getPlayerColor(), UnitType.KAVALLERIE, 1, UnitState.INACTIVE, b.x, b.y));
		});
		spawnInfanterie = new ImageButton(w, TextureHandler.getImagePng("unit_" + c.game.getPlayerColor().toString().toLowerCase() + "_infanterie"), e -> {
			game.setOverlay(null);
			c.spawnUnit(new Unit(c.game.getPlayerColor(), UnitType.INFANTERIE, 1, UnitState.INACTIVE, b.x, b.y));
		});

		this.add(spawnTank);
		spawnTank.setBounds(5, 5, 120 / 2, 140 / 2);

		this.add(spawnArtillerie);
		spawnArtillerie.setBounds(120 / 2 + 10, 5, 120 / 2, 140 / 2);

		this.add(spawnInfanterie);
		spawnInfanterie.setBounds(5, 140 / 2 + 10, 120 / 2, 140 / 2);

		this.add(spawnKavallerie);
		spawnKavallerie.setBounds(120 / 2 + 10, 140 / 2 + 10, 120 / 2, 140 / 2);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				if (x <= 0 || x >= 120 + 15 || y <= 0 || y >= 140 + 15) selType = null;
				else if (x < 120 / 2 + 5 && y < 140 / 2 + 5) selType = UnitType.PANZER;
				else if (x >= 120 / 2 + 5 && y < 140 / 2 + 5) selType = UnitType.ARTILLERIE;
				else if (x < 120 / 2 + 5 && y >= 140 / 2 + 5) selType = UnitType.INFANTERIE;
				else if (x >= 120 / 2 + 5 && y >= 140 / 2 + 5) selType = UnitType.KAVALLERIE;
			}
		});

		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (x < 0 || x > 120 + 15 || y < 0 || y > 140 + 15) selType = null;

				else if (x < 120 / 2 + 5 && y < 140 / 2 + 5) selType = UnitType.PANZER;
				else if (x >= 120 / 2 + 5 && y < 140 / 2 + 5) selType = UnitType.ARTILLERIE;
				else if (x < 120 / 2 + 5 && y >= 140 / 2 + 5) selType = UnitType.INFANTERIE;
				else if (x >= 120 / 2 + 5 && y >= 140 / 2 + 5) selType = UnitType.KAVALLERIE;
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(GUIConstants.COLOR_OVERLAY_BACKGROUND);
		g.fillRect(0, 0, 120 + 15, getHeight());

		if (selType != null) {
			g.fillRect(120 + 20, 0, 130, getHeight());
			g.setColor(Color.WHITE);
			int spacing = 17;
			g.drawString(LanguageHandler.get(selType.getDisplayName()), 120 + 20, spacing);
			g.drawString(LanguageHandler.get("Attack") + ": " + selType.getAttack(), 120 + 20, 2 * spacing);
			g.drawString(LanguageHandler.get("Defence") + ": " + selType.getDefence(), 120 + 20, 3 * spacing);
			g.drawString(LanguageHandler.get("Health") + ": " + selType.getHealth(), 120 + 20, 4 * spacing);
			g.drawString(LanguageHandler.get("Costs") + ": " + selType.getCost(), 120 + 20, 5 * spacing);
			g.drawString(LanguageHandler.get("Attack Range") + ": " + selType.getMinAttackDistance() + " - " + selType.getMaxAttackDistance(), 120 + 20, 6 * spacing);
			g.drawString(LanguageHandler.get("Movement Range") + ": " + selType.getMovementDistance(), 120 + 20, 7 * spacing);
			g.drawString(LanguageHandler.get("Stack Size") + ": " + selType.getMaxStackSize(), 120 + 20, 8 * spacing);

		}
	}

	@Override
	public boolean destroyable() {
		return true;
	}
}
