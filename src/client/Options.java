package client;

import client.audio.AudioConstants;
import client.window.GUIConstants;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Options {

    private static final File DATA_FILE = new File("res/files/options/options.yml");

    public static void save() {
        DumperOptions op = new DumperOptions();
        op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(op);

        Map<String, Object> data = new HashMap<>();

        Map<String, Float> audioData = new HashMap<>();
        audioData.put("Music", AudioConstants.MUSIC_VOLUME);
        audioData.put("Effects", AudioConstants.EFFECT_VOLUME);

        Map<String, Boolean> guiData = new HashMap<>();
        guiData.put("AA", GUIConstants.VALUE_ANTIALIASING.equals(RenderingHints.VALUE_ANTIALIAS_ON));

        Map<String, String> inputData = new HashMap<>();
        inputData.put("Username", GUIConstants.LAST_USERNAME);
        inputData.put("IP", GUIConstants.LAST_IP);
        inputData.put("Port", GUIConstants.LAST_PORT);

        data.put("Audio", audioData);
        data.put("Graphics", guiData);
        data.put("Inputs", inputData);

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
                AudioConstants.MUSIC_VOLUME = audioData.get("Music").floatValue();
                AudioConstants.EFFECT_VOLUME = audioData.get("Effects").floatValue();

                Map<String, Boolean> guiData = (Map<String, Boolean>)data.get("Graphics");
                GUIConstants.VALUE_ANTIALIASING = guiData.get("AA")? RenderingHints.VALUE_ANTIALIAS_ON: RenderingHints.VALUE_ANTIALIAS_OFF;

                Map<String, String> inputData = (Map<String, String>) data.get("Inputs");
                if(inputData != null) {
                    GUIConstants.LAST_PORT = inputData.get("Port");
                    GUIConstants.LAST_USERNAME = inputData.get("Username");
                    GUIConstants.LAST_IP = inputData.get("IP");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
