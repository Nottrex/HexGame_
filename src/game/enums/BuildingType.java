package game.enums;

public enum BuildingType {
	BASE(10);

	private int viewDistance;
	BuildingType(int viewDistance){
		this.viewDistance = viewDistance;
	}

	public int getViewDistance() {
		return viewDistance;
	}
}
