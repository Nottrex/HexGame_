package view;

import game.Game;
import game.GameMap;
import game.Location;
import game.Unit;
import game.enums.Direction;
import game.enums.PlayerColor;
import game.enums.UnitState;
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
		game.nextRound();
		localPlayers = new ArrayList<>();
		localPlayers.add(PlayerColor.BLUE);
		localPlayers.add(PlayerColor.RED);

		this.window = window;
	}

	protected void onMouseClick(Location l) {
		if (game == null) return;

		GameMap m = game.getMap();
		if (selecetedField == null) {
			if (game.getMap().getFieldAt(l).isAccessible())
				selecetedField = l;
		} else {
			if (game.getMap().getFieldAt(l).isAccessible()) {
				Optional<Unit> u = m.getUnitAt(selecetedField);
				Optional<Unit> u2 = m.getUnitAt(l);

				if (u.isPresent() && u.get().getPlayer() == game.getPlayerTurn()) {
					Unit unit = u.get();
					pa = ActionUtil.getPossibleActions(game, unit);

					if (u2.isPresent() && pa.canAttack().contains(l)) {
						List<Direction> movement = pa.moveToToAttack(l);

						Location a = selecetedField;
						for (Direction d: movement) {
							a = d.applyMovement(a);
						}

						unit.setX(a.x);
						unit.setY(a.y);

						//TODO: ATTACK UNITS?

						unit.setState(UnitState.INACTIVE);
						selecetedField = null;
					} else if (pa.canMoveTo().contains(l)) {
						unit.setX(l.x);
						unit.setY(l.y);

						unit.setState(UnitState.MOVED);
						selecetedField = null;
					} else selecetedField = l;
				} else selecetedField = l;
			} else selecetedField = null;
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
