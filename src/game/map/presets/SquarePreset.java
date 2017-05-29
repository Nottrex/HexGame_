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
        Field[][] out = new Field[width+(height/2)][height];

        for (int x = 0; x < (width+(height/2)); x++) {
            for (int y = 0; y < height; y++) {
                if (y % 2 == 0) {
                    if (x < (y/2) || x > ((y/2)+width)-2) {
                        out[x][y] = Field.VOID;
                    }
                } else {
                   if (x < ((y+1)/2) || x > (((y+1)/2)+(width-1))-2) {
                       out[x][y] = Field.VOID;
                   }
                }
            }
        }

        return out;
    }

    @Override
    public List<Location> getSpawnPoints() {
        List<Location> locations = new ArrayList<>();

        locations.add(new Location(3, 2));
        locations.add(new Location(width-5 + height/2, height-3));
        locations.add(new Location(1 + height/2, height-3));
        locations.add(new Location(width-3, 2));

        return locations;
    }
}
