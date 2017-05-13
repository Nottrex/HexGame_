package client;

import client.audio.AudioConstants;
import client.i18n.LanguageHandler;
import client.window.GUIConstants;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Options {

    //GUI Options
    public static Object VALUE_ANTIALIASING 				= RenderingHints.VALUE_ANTIALIAS_ON;
    public static String LAST_USERNAME						= null;
    public static String LAST_IP							= null;
    public static String LAST_PORT							= null;

    //Audio Options
    public static float MUSIC_VOLUME                        = -20.0f;
    public static float EFFECT_VOLUME 	    	    		= -20.0f;

    //Language Options
    public static String language = "English";

    public static final File DATA_FILE_FOLDER = new File(System.getProperty("user.dir") + File.separator + "hexgame" + File.separator);
    private static final File DATA_FILE = new File(System.getProperty("user.dir") + File.separator + "hexgame" + File.separator + "options.yml");

    static {
        DATA_FILE_FOLDER.mkdirs();
    }

    public static void save() {
        DumperOptions op = new DumperOptions();
        op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(op);

        Map<String, Object> data = new HashMap<>();

        Map<String, Float> audioData = new HashMap<>();
        audioData.put("Music", MUSIC_VOLUME);
        audioData.put("Effects", EFFECT_VOLUME);

        Map<String, Boolean> guiData = new HashMap<>();
        guiData.put("AA", VALUE_ANTIALIASING.equals(RenderingHints.VALUE_ANTIALIAS_ON));

        Map<String, String> inputData = new HashMap<>();
        inputData.put("Username", LAST_USERNAME);
        inputData.put("IP", LAST_IP);
        inputData.put("Port", LAST_PORT);

        data.put("Audio", audioData);
        data.put("Graphics", guiData);
        data.put("Inputs", inputData);
        data.put("Language", language);

        try {
            yaml.dump(data, new FileWriter(DATA_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        DumperOptions op = new DumperOptions();
        op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(op);
        if(DATA_FILE.exists()) {

            try {
                Map<String, Object> data = (Map<String, Object>) yaml.load(new FileInputStream(DATA_FILE));

                Map<String, Double> audioData = (Map<String, Double>) data.get("Audio");
                MUSIC_VOLUME = audioData.get("Music").floatValue();
                EFFECT_VOLUME = audioData.get("Effects").floatValue();

                Map<String, Boolean> guiData = (Map<String, Boolean>)data.get("Graphics");
                VALUE_ANTIALIASING = guiData.get("AA")? RenderingHints.VALUE_ANTIALIAS_ON: RenderingHints.VALUE_ANTIALIAS_OFF;

                Map<String, String> inputData = (Map<String, String>) data.get("Inputs");
                if(inputData != null) {
                    LAST_PORT = inputData.get("Port");
                    LAST_USERNAME = inputData.get("Username");
                    LAST_IP = inputData.get("IP");
                }

                language = (String) data.get("Language");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
