package client;

import game.Game;

public abstract class AnimationAction {
	protected Game game;

	public AnimationAction(Game game) {
		this.game = game;
	}

	public void update(long currentTime) {

	}

	public abstract long getTotalTime();
	public abstract void finish();
}
