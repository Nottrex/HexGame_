package client.window.animationActions;

import client.game.Camera;
import game.Game;

public abstract class AnimationAction {
	protected Game game;
	protected Camera camera;

	public AnimationAction(Game game, Camera camera) {
		this.game = game;
		this.camera = camera;
	}

	/**
	 * Can be called multiple times during drawing to calculate progress of animation
	 *
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
