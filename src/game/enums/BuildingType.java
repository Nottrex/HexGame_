package game.enums;

public enum BuildingType {
	BASE(10, 100);

	private int viewDistance, gain;
	BuildingType(int viewDistance, int gain){
		this.viewDistance = viewDistance;
		this.gain = gain;
	}

	public int getViewDistance() {
		return viewDistance;
	}

	public int getGain() {
		return gain;
	}
}
