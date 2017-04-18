package game.map.presets;

import game.Location;
import game.enums.Field;

import java.util.ArrayList;
import java.util.List;

public class HexPreset implements MapPreset{

    private int width, height;
    public HexPreset(int width) {
        this.width = width;
        this.height = width;

        if (width <= 10) {
            throw new RuntimeException("Width to small (min 11): " + width);
        }
        if (width % 2 != 1) {
            throw new RuntimeException("Width has to be odd: " + width);
        }
    }

    @Override
    public Field[][] getPresetMap() {
        Field[][] out = new Field[width][height];

        for(int x = 0; x < out.length; x++) {
            for (int y = 0; y < out[0].length; y++) {
                if(x < y - height/2 || x > y + height/2) out[x][y] = Field.VOID;
            }
        }

        return out;
    }

    @Override
    public List<Location> getSpawnPoints() {
        int a = width /2;
        List<Location> locations = new ArrayList<>();

        locations.add(new Location(2, 2));
        locations.add(new Location(width-3, width-3));
        locations.add(new Location(2, a));
        locations.add(new Location(a, 2));
        locations.add(new Location(width-3, a));
        locations.add(new Location(a, width-3));

        return locations;
    }
}
