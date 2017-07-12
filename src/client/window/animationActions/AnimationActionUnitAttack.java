package client.window.animationActions;

import client.game.Camera;
import game.Game;
import game.Unit;

public class AnimationActionUnitAttack extends AnimationAction {
	private Unit unit, target;

	public AnimationActionUnitAttack(Game game, Camera camera, Unit unit, Unit target) {
		super(game, camera);
		this.unit = unit;
		this.target = target;
	}

	@Override
	public long getTotalTime() {
		return 0;
	}

	@Override
	public void finish() {
		camera.addScreenshake(0.1f);
		game.getMap().attack(unit, target);
	}
}
