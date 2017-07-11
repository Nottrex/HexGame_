package client.window.animationActions;

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
	private int z;
	private double last = 0;

	public AnimationActionUnitMove(Game game, Unit unit, int targetX, int targetY, List<Direction> movements) {
		super(game);

		this.unit = game.getMap().getGameUnit(unit);
		this.targetX = targetX;
		this.targetY = targetY;
		this.movements = movements;

		z = TILE_TIME * movements.size();
	}

	@Override
	public long getTotalTime() {
		return z;
	}

	@Override
	public void update(long currentTime) {
		double current = getDistance(currentTime);

		for (int i = (int) (last / TILE_TIME); i < (int) (current / TILE_TIME); i++) {
			Location newField = movements.get(i).applyMovement(new Location(unit.getX(), unit.getY()));
			unit.setX(newField.x);
			unit.setY(newField.y);
		}
		last = current;
	}

	/**
	 * @return Unit used for the animation
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * @return Direction in which the Unit is moving
	 */
	public Direction getCurrentDirection() {
		return movements.get((int) (last / TILE_TIME));
	}

	/**
	 * Simple interpolation method to calculate values between two timestamps
	 *
	 * @return
	 */
	public double interpolation() {
		return finish ? 0 : ((last - ((int) (last / TILE_TIME)) * TILE_TIME) * 1.0) / TILE_TIME;
	}

	/**
	 * Converts time to current distance
	 *
	 * @param time current time after animation start
	 * @return current distance
	 */
	public double getDistance(double time) {
		return (-2.0 / (z * z)) * time * time * time + (3.0 / z) * time * time;
	}

	@Override
	public void finish() {
		unit.moveTo(targetX, targetY);
		finish = true;
	}
}
