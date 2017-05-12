package client.i18n;

import client.i18n.Strings;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class LanguageLoader {

	public static String language = "English";
	public static String LANGUAGE_FOLDER = System.getProperty("user.dir") + "/src/res/language/";

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

		Strings.set(data);
	}
}
