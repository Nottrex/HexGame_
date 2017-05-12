package client.i18n;

import client.FileHandler;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class LanguageHandler {

	private static Map<String, String> dictionary = new HashMap<>();

	public static String get(String key) {
		if(dictionary.containsKey(key)) return dictionary.get(key);
		else return key;
	}

	public static void set(Map<String, String> dict) {
		dictionary = dict;
	}

	public static String language = "Deutsch";
	public static String LANGUAGE_FOLDER = "/language/";

	public static void load() {

		DumperOptions op = new DumperOptions();
		op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(op);

		Map<String, String> data;

		try{
			String dataString = FileHandler.loadFile(LANGUAGE_FOLDER + language + ".yml");
			data = (Map<String, String>) yaml.load(dataString);
		}catch(Exception e) {
			data = new HashMap<>();
			System.err.println("Error reading File: " + language);
		}

		LanguageHandler.set(data);
	}
}
