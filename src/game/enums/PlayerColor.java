package game.enums;

public enum PlayerColor {
	BLUE("Blue"), RED("Red"), GREEN("Green"), YELLOW("Yellow");

	private String displayName;
	PlayerColor(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
