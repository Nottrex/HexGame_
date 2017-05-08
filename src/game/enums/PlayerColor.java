package game.enums;

import i18n.Strings;

/**
 * Colors that a player can choose
 */
public enum PlayerColor {
	BLUE(Strings.get("Blue")), RED(Strings.get("Red")), GREEN(Strings.get("Green")), YELLOW(Strings.get("Yellow"));

	private String displayName;
	PlayerColor(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
