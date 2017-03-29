package client.animationActions;

import client.AnimationAction;
import game.Game;

public class AnimationActionRoundFinish extends AnimationAction {
	public AnimationActionRoundFinish(Game game) {
		super(game);
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
