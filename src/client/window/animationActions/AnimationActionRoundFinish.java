package client.window.animationActions;

import client.game.Camera;
import game.Game;

/*
	We need it later => Animation on round end
 */

public class AnimationActionRoundFinish extends AnimationAction {
	public AnimationActionRoundFinish(Game game, Camera camera) {
		super(game, camera);
	}

	@Override
	public long getTotalTime() {
		return 0;
	}

	@Override
	public void finish() {
		game.nextPlayer();
	}
}
