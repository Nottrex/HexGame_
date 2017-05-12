package client.i18n;

import java.util.HashMap;
import java.util.Map;

public class Strings {

	private static Map<String, String> dictionary = new HashMap<>();

	public static String get(String key) {
		if(dictionary.containsKey(key)) return dictionary.get(key);
		else return key;
	}

	public static void set(Map<String, String> dict) {
		dictionary = dict;
	}
}
