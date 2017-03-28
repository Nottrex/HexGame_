package client.animationActions;

import client.AnimationAction;
import game.Game;
import game.Location;
import game.Unit;
import game.enums.Direction;

import java.util.List;

public class AnimationActionUnitMove extends AnimationAction {
	private static final int TILE_TIME = 500;

	private Unit unit;
	private int targetX, targetY;
	private List<Direction> movements;
	private boolean finish = false;

	public AnimationActionUnitMove(Game game, Unit unit, int targetX, int targetY, List<Direction> movements) {
		super(game);

		this.unit = unit;
		this.targetX = targetX;
		this.targetY = targetY;
		this.movements = movements;
	}

	@Override
	public long getTotalTime() {
		return TILE_TIME*movements.size();
	}

	private int lastTime = 0;
	@Override
	public void update(long currentTime) {
		for (int i = lastTime/TILE_TIME; i < currentTime/TILE_TIME; i++) {
			Location newField = movements.get(i).applyMovement(new Location(unit.getX(), unit.getY()));
			unit.setX(newField.x);
			unit.setY(newField.y);
		}
		lastTime = (int) currentTime;
	}

	public Unit getUnit() {
		return unit;
	}

	public Direction getCurrentDirection() {
		return movements.get(lastTime/TILE_TIME);
	}

	public double interpolation() {
		return finish ? 0 : ((lastTime - (lastTime/TILE_TIME)*TILE_TIME)*1.0)/TILE_TIME;
	}

	@Override
	public void finish() {
		unit.moveTo(targetX, targetY);
		finish = true;
	}
}
