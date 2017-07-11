package client.i18n;

import client.FileHandler;
import client.Options;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class LanguageHandler {
	public static final String CUSTOM_LANGUAGE_PATH = Options.DATA_FILE_PATH + "languages" + File.separator;
	private static Map<String, String> dictionary = new HashMap<>();

	static {
		new File(CUSTOM_LANGUAGE_PATH).mkdirs();
	}

	public static void load() {
		DumperOptions op = new DumperOptions();
		op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(op);

		Map<String, String> data = null;

		if (availableSystemLanguages().contains(Options.language)) {
			try {
				String dataString = FileHandler.loadFile("language/" + Options.language + ".yml");
				data = (Map<String, String>) yaml.load(dataString);
			} catch (Exception e) {
				data = new HashMap<>();
				System.err.println("Error reading System Language: " + Options.language);
			}
		} else if (availableCustomLanguages().contains(Options.language)) {
			try {
				data = (Map<String, String>) yaml.load(new FileInputStream(new File(CUSTOM_LANGUAGE_PATH + Options.language + ".yml")));
			} catch (Exception e) {
				data = new HashMap<>();
				System.err.println("Error reading Custom Language: " + Options.language);
			}
		} else {
			data = new HashMap<>();
			System.err.println("No such language: " + Options.language);
		}

		LanguageHandler.set(data);
	}

	public static List<String> availableLanguages() {
		List<String> list = availableSystemLanguages();
		list.addAll(availableCustomLanguages());
		return list;
	}

	private static List<String> availableSystemLanguages() {
		List<String> languages = new ArrayList<>();
		Scanner files = new Scanner(FileHandler.loadFile("language/languages.txt"));
		while (files.hasNextLine()) {
			languages.add(files.nextLine().split("\\.")[0]);
		}
		files.close();

		return languages;
	}

	private static List<String> availableCustomLanguages() {
		List<String> languages = new ArrayList<>();
		File f = new File(CUSTOM_LANGUAGE_PATH);

		for (File f2 : f.listFiles()) {
			if (f2.isFile()) {
				String[] parts = f2.getName().split("\\.");
				if (parts[parts.length - 1].equalsIgnoreCase("yml")) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < parts.length - 1; i++) {
						sb.append(parts[i]);
					}
					languages.add(sb.toString());
				}
			}
		}

		return languages;
	}

	public static String get(String key) {
		if (dictionary.containsKey(key)) return dictionary.get(key);
		else return key;
	}

	public static void set(Map<String, String> dict) {
		dictionary = dict;
	}
}
