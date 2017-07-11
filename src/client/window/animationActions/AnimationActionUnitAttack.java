package client.window.animationActions;

import game.Game;
import game.Unit;

public class AnimationActionUnitAttack extends AnimationAction {
	private Unit unit, target;

	public AnimationActionUnitAttack(Game game, Unit unit, Unit target) {
		super(game);
		this.unit = unit;
		this.target = target;
	}

	@Override
	public long getTotalTime() {
		return 0;
	}

	@Override
	public void finish() {
		game.getMap().attack(unit, target);
	}
}
