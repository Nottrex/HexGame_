package game.enums;

import i18n.Strings;

/**
 * Colors that a player can choose
 */
public enum PlayerColor {
	BLUE(Strings.COLOR_BLUE), RED(Strings.COLOR_RED), GREEN(Strings.COLOR_GREEN), YELLOW(Strings.COLOR_YELLOW);

	private String displayName;
	PlayerColor(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
