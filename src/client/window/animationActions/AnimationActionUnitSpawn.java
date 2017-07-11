package client.window.animationActions;

import game.Game;
import game.Unit;

public class AnimationActionUnitSpawn extends AnimationAction {
	private Unit unit;

	public AnimationActionUnitSpawn(Game game, Unit unit) {
		super(game);
		this.unit = unit;
	}

	@Override
	public long getTotalTime() {
		return 0;
	}

	@Override
	public void finish() {
		game.getMap().spawnUnit(unit);
	}
}
