package game.map.presets;

import game.Location;
import game.enums.Field;

import java.util.List;

/**
 * Presets for the map generation
 * All fields that are not void will be replaced
 */
public interface MapPreset {

    public Field[][] getPresetMap();

    public List<Location> getSpawnPoints();
}
