package game.map.presets;

import game.enums.Field;

/**
 * Presets for the map generation
 * All fields that are not void will be replaced
 */
public interface MapPreset {

    public Field[][] getPresetMap();
}
