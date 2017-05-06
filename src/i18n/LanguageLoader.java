package i18n;

import client.Options;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class LanguageLoader {

	public static String language = "English";
	public static String LANGUAGE_FOLDER = Options.DATA_FILE_FOLDER.getAbsolutePath() + File.separator + "language" + File.separator;

	public static void load() {

		DumperOptions op = new DumperOptions();
		op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(op);

		File languageFile = new File(LANGUAGE_FOLDER + language + ".yml");
		System.out.println(languageFile.getAbsolutePath());
		Map<String, String> data;

		try{
			data = (Map<String, String>) yaml.load(new FileInputStream(languageFile));
		}catch(Exception e) {
			data = new HashMap<>();
		}

		if(data.containsKey("Tank")) Strings.UNIT_NAME_TANK = data.get("Tank");
		else Strings.UNIT_NAME_TANK = "Tank";

		if(data.containsKey("Cavalry")) Strings.UNIT_NAME_CAVALRY = data.get("Cavalry");
		else Strings.UNIT_NAME_CAVALRY = "Cavalry";

		if(data.containsKey("Infantry")) Strings.UNIT_NAME_INFANTRY = data.get("Infantry");
		else Strings.UNIT_NAME_INFANTRY = "Infantry";

		if(data.containsKey("Artillery")) Strings.UNIT_NAME_ARTILLERY = data.get("Artillery");
		else Strings.UNIT_NAME_ARTILLERY = "Artillery";

		if(data.containsKey("Water")) Strings.FIELD_NAME_WATER = data.get("Water");
		else Strings.FIELD_NAME_WATER = "Water";

		if(data.containsKey("Forest")) Strings.FIELD_NAME_FOREST = data.get("Forest");
		else Strings.FIELD_NAME_FOREST = "Forest";

		if(data.containsKey("Dirt")) Strings.FIELD_NAME_DIRT = data.get("Dirt");
		else Strings.FIELD_NAME_DIRT = "Dirt";

		if(data.containsKey("Snow")) Strings.FIELD_NAME_SNOW = data.get("Snow");
		else Strings.FIELD_NAME_SNOW = "Snow";

		if(data.containsKey("Sand")) Strings.FIELD_NAME_SAND = data.get("Sand");
		else Strings.FIELD_NAME_SAND = "Sand";

		if(data.containsKey("Stone")) Strings.FIELD_NAME_STONE = data.get("Stone");
		else Strings.FIELD_NAME_STONE = "Stone";

		if(data.containsKey("Grass_Rock")) Strings.FIELD_NAME_GRASS_ROCK = data.get("Grass_Rock");
		else Strings.FIELD_NAME_GRASS_ROCK = "Grass_Rock";

		if(data.containsKey("Dirt_Rock")) Strings.FIELD_NAME_DIRT_ROCK = data.get("Dirt_Rock");
		else Strings.FIELD_NAME_DIRT_ROCK = "Dirt_Rock";

		if(data.containsKey("Grass")) Strings.FIELD_NAME_GRASS = data.get("Grass");
		else Strings.FIELD_NAME_GRASS = "Grass";

		if(data.containsKey("Void")) Strings.FIELD_NAME_VOID = data.get("Void");
		else Strings.FIELD_NAME_VOID = "Void";

		if(data.containsKey("Red")) Strings.COLOR_RED = data.get("Red");
		else Strings.COLOR_RED = "Red";

		if(data.containsKey("Green")) Strings.COLOR_GREEN = data.get("Green");
		else Strings.COLOR_GREEN = "Green";

		if(data.containsKey("Yellow")) Strings.COLOR_YELLOW = data.get("Yellow");
		else Strings.COLOR_YELLOW = "Yellow";

		if(data.containsKey("Blue")) Strings.COLOR_BLUE = data.get("Blue");
		else Strings.COLOR_BLUE = "Blue";

		if(data.containsKey("Join Game")) Strings.BUTTON_TEXT_JOIN_GAME = data.get("Join Game");
		else Strings.BUTTON_TEXT_JOIN_GAME = "Join Game";

		if(data.containsKey("Create Game")) Strings.BUTTON_TEXT_CREATE_GAME = data.get("Create Game");
		else Strings.BUTTON_TEXT_CREATE_GAME = "Create Game";

		if(data.containsKey("Connect")) Strings.BUTTON_TEXT_CONNECT = data.get("Connect");
		else Strings.BUTTON_TEXT_CONNECT = "Connect";

		if(data.containsKey("Back to Mainmenu")) Strings.BUTTON_TEXT_MAINMENU = data.get("Back to Mainmenu");
		else Strings.BUTTON_TEXT_MAINMENU = "Back to Mainmenu";

		if(data.containsKey("Toggle Ready")) Strings.BUTTON_TEXT_TOGGLE_READY = data.get("Toggle Ready");
		else Strings.BUTTON_TEXT_TOGGLE_READY = "Toggle Ready";

		if(data.containsKey("Toggle Color")) Strings.BUTTON_TEXT_TOOGLE_COLOR = data.get("Toggle Color");
		else Strings.BUTTON_TEXT_TOOGLE_COLOR = "Toggle Color";

		if(data.containsKey("Quit")) Strings.BUTTON_TEXT_QUIT = data.get("Quit");
		else Strings.BUTTON_TEXT_QUIT = "Quit";

		if(data.containsKey("Exit Game")) Strings.BUTTON_TEXT_EXIT = data.get("Exit Game");
		else Strings.BUTTON_TEXT_EXIT = "Exit Game";

		if(data.containsKey("Accept")) Strings.BUTTON_TEXT_ACCEPT = data.get("Accept");
		else Strings.BUTTON_TEXT_ACCEPT = "Accept";

		if(data.containsKey("Cancel")) Strings.BUTTON_TEXT_CANCEL = data.get("Cancel");
		else Strings.BUTTON_TEXT_CANCEL = "Cancel";

		if(data.containsKey("Square")) Strings.MAP_TYPE_SQUARE = data.get("Square");
		else Strings.MAP_TYPE_SQUARE = "Square";

		if(data.containsKey("Oval")) Strings.MAP_TYPE_OVAL = data.get("Oval");
		else Strings.MAP_TYPE_OVAL = "Oval";

		if(data.containsKey("Hexgonal")) Strings.MAP_TYPE_HEXAGON = data.get("Hexgonal");
		else Strings.MAP_TYPE_HEXAGON = "Hexgonal";

		if(data.containsKey("Effects Volume")) Strings.LABEL_EFFECTS_VOLUME = data.get("Effects Volume");
		else Strings.LABEL_EFFECTS_VOLUME = "Effects Volume";

		if(data.containsKey("Music Volume")) Strings.LABEL_MUSIC_VOLUME = data.get("Music Volume");
		else Strings.LABEL_MUSIC_VOLUME = "Music Volume";

		if(data.containsKey("Use AA")) Strings.LABEL_USE_AA = data.get("Use AA");
		else Strings.LABEL_USE_AA = "Use AA";

		if(data.containsKey("Width")) Strings.WIDTH = data.get("Width");
		else Strings.WIDTH = "Width";

		if(data.containsKey("Height")) Strings.HEIGHT = data.get("Height");
		else Strings.HEIGHT = "Height";

		if(data.containsKey("Round")) Strings.GAME_ROUND = data.get("Round");
		else Strings.GAME_ROUND = "Round";

		if(data.containsKey("Costs")) Strings.GAME_TILE_COSTS = data.get("Costs");
		else Strings.GAME_TILE_COSTS = "Costs";
	}
}
