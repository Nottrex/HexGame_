package game.enums;

public enum PlayerColor {
	BLUE("blue", "Blue"), RED("red", "Red"), GREEN("green", "Green"), GREY("grey", "Grey");

	private String textureName, displayName;
	PlayerColor(String textureName, String displayName) {
		this.textureName = textureName;
		this.displayName = displayName;
	}

	public String getTextureName() {
		return textureName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
