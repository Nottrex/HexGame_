package client.window.animationActions;

import client.game.Camera;
import game.Game;
import game.Unit;

public class AnimationActionUnitSpawn extends AnimationAction {
	private Unit unit;

	public AnimationActionUnitSpawn(Game game, Camera camera, Unit unit) {
		super(game, camera);
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
