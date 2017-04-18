package game.map.presets;

import game.Location;
import game.enums.Field;

import java.util.ArrayList;
import java.util.List;

public class SquarePreset implements  MapPreset{

    private int width, height;
    public SquarePreset(int width, int height) {
        this.width = width;
        this.height = height;

        if (width < 11 || height < 11) {
            throw new RuntimeException("Width or height to small (min 11): " + width + " " + height);
        }
    }


    @Override
    public Field[][] getPresetMap() {
        return new Field[width][height];
    }

    @Override
    public List<Location> getSpawnPoints() {
        List<Location> locations = new ArrayList<>();

        locations.add(new Location(2, 2));
        locations.add(new Location(width-3, height-3));
        locations.add(new Location(2, height-3));
        locations.add(new Location(width-3, 2));

        return locations;
    }
}
