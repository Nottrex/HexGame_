package view;

import game.Game;
import game.GameMap;
import game.Location;
import game.Unit;
import game.enums.Field;
import game.enums.PlayerColor;
import game.util.ActionUtil;
import game.util.PossibleActions;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {

	private Window window;	//TODO Remove everything window related!

	protected Game game;
	protected List<PlayerColor> localPlayers;
	protected Location selecetedField = null;
	protected PossibleActions pa = null;

	public Controller(Window window) {
		game = new Game();
		localPlayers = new ArrayList<>();
		localPlayers.add(PlayerColor.BLUE);
		localPlayers.add(PlayerColor.RED);

		this.window = window;
	}

	protected void onMouseClick(Location l) {
		if (game == null) return;

		GameMap m = game.getMap();
		if (selecetedField == null) {
			if (game.getMap().getFieldAt(l) != Field.VOID)
				selecetedField = l;
		} else {


			if (game.getMap().getFieldAt(l) == Field.VOID) {
				selecetedField = null;
			} else {
				Optional<Unit> u = m.getUnitAt(selecetedField);
				Optional<Unit> u2 = m.getUnitAt(l);

				if (u.isPresent() && !u2.isPresent() && u.get().getPlayer() == game.getPlayerTurn()) {
					pa = ActionUtil.getPossibleActions(game, u.get());

					if (pa.canMoveTo().contains(l)) {
						u.get().setX(l.x);
						u.get().setY(l.y);
					}

					selecetedField = null;
				} else if (u.isPresent() && u2.isPresent()) {
					pa = ActionUtil.getPossibleActions(game, u.get());
					if (u.get().getPlayer() == game.getPlayerTurn() && pa.canAttack().contains(l)) {
						//TODO: ATTACK UNITS?
					} else selecetedField = l;
				} else selecetedField = l;
			}
		}

		if (selecetedField != null) {
			Optional<Unit> u = m.getUnitAt(selecetedField);
			if (u.isPresent()) pa = ActionUtil.getPossibleActions(game, u.get());
		}

		window.redrawInfoBar();
	}

	protected void onKeyType(int keyCode) {
		if (keyCode == KeyEvent.VK_ENTER) {
			game.nextPlayer();
			window.redrawTopBar();
		}
	}
}
