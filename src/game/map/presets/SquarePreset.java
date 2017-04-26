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
        Field[][] out = new Field[width*2][height];

        for(int  x = 0; x < width*2; x++) {
            for(int  y = 0; y < height; y++) {
                out[x][y] = Field.VOID;
            }
        }

        for(int  x = 0; x < width; x++) {
            for(int  y = 0; y < height; y++) {
                out[x+y/2][y] = null;
            }
        }

        return out;
    }

    @Override
    public List<Location> getSpawnPoints() {
        List<Location> locations = new ArrayList<>();

        locations.add(new Location(2, 2));
        locations.add(new Location(width-3, height-3)); //TODO: Fix
        locations.add(new Location(2, height-3));
        locations.add(new Location(width-3, 2));        //TODO: Fix

        return locations;
    }
}
