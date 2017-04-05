package client;

import game.Game;

public abstract class AnimationAction {
	protected Game game;

	public AnimationAction(Game game) {
		this.game = game;
	}

	/**
	 * Can be called multiple times during drawing to calculate progress of animation
	 * @param currentTime after beginning of the animation
	 */
	public void update(long currentTime) {

	}

	/**
	 * @return Animation duration in ms
	 */
	public abstract long getTotalTime();

	/**
	 * Called when animation ends
	 */
	public abstract void finish();
}
