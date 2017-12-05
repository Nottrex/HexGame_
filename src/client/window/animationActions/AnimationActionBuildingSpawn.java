package client.window.animationActions;

import client.game.Camera;
import game.Building;
import game.Game;
import game.Unit;

public class AnimationActionBuildingSpawn extends AnimationAction {
	private Building building;

	public AnimationActionBuildingSpawn(Game game, Camera camera, Building building) {
		super(game, camera);
		this.building = building;
	}

	@Override
	public long getTotalTime() {
		return 0;
	}

	@Override
	public void finish() {
		game.getMap().spawnBuilding(building);
	}
}
