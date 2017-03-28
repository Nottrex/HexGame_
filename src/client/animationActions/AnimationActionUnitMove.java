package client.animationActions;

import client.AnimationAction;
import game.Game;
import game.Unit;
import game.enums.Direction;

import java.util.List;

public class AnimationActionUnitMove extends AnimationAction {
	private Unit unit;
	private int targetX, targetY;
	private List<Direction> movements;

	public AnimationActionUnitMove(Game game, Unit unit, int targetX, int targetY, List<Direction> movements) {
		super(game);

		this.unit = unit;
		this.targetX = targetX;
		this.targetY = targetY;
		this.movements = movements;
	}

	@Override
	public long getTotalTime() {
		return 0;
	}

	@Override
	public void finish() {
		unit.moveTo(targetX, targetY);
	}
}
