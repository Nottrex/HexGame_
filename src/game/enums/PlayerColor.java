package game.enums;

import client.i18n.LanguageHandler;

/**
 * Colors that a player can choose
 */
public enum PlayerColor {
	BLUE(LanguageHandler.get("Blue")), RED(LanguageHandler.get("Red")), GREEN(LanguageHandler.get("Green")), YELLOW(LanguageHandler.get("Yellow"));

	private String displayName;
	PlayerColor(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
