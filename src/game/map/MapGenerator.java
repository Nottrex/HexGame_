package game.map;

import game.Location;
import game.enums.Direction;
import game.enums.Field;
import game.map.presets.MapPreset;

import java.util.List;
import java.util.Random;

public class MapGenerator {

    private ValueNoise_2D vn;
    private MapPreset mp;

    public MapGenerator(MapPreset mp) {
        this.mp = mp;
        this.vn = new ValueNoise_2D(mp.getPresetMap().length, mp.getPresetMap()[0].length);

        vn.calculate();
    }

    public List<Location> getSpawnPoints() {
        return mp.getSpawnPoints();
    }

    public Field[][] getMap() {
        Field[][] out = new Field[mp.getPresetMap().length][mp.getPresetMap()[0].length];

        for(int x = 0; x < out.length; x++) {
            for(int y = 0; y < out[0].length; y++){
                if(mp.getPresetMap()[x][y] == Field.VOID) {
                    out[x][y] = Field.VOID;
                    continue;
                }

                float f = vn.getHeightMap()[x][y];

                if(f <= 1.5f/9.0f) out[x][y] = Field.WATER;
                else if(f <= 2.4f/9.0f)out[x][y] = Field.SAND;
                else if(f <= 3.8f/9.0f)out[x][y] = Field.GRASS;
                else if(f <= 4.8f/9.0f)out[x][y] = Field.FOREST;
                else if(f <= 5.4f/9.0f)out[x][y] = Field.GRASS_ROCK;
                else if(f <= 6.0f/9.0f)out[x][y] = Field.DIRT;
                else if(f <= 7.0f/9.0f)out[x][y] = Field.DIRT_ROCK;
                else if(f <= 8.0f/9.0f)out[x][y] = Field.STONE;
                else if(f <= 9.0f/9.0f)out[x][y] = Field.SNOW;
            }
        }

        for (Location l: mp.getSpawnPoints()) {
            out[l.x][l.y] = Field.GRASS;

            for (Direction d: Direction.values()) {
                Location l2 = d.applyMovement(l);
                out[l2.x][l2.y] = Field.GRASS;
            }
        }

        return out;
    }

}
