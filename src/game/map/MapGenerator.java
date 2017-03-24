package game.map;

import game.enums.Field;
import game.map.presets.MapPreset;

public class MapGenerator {

    private ValueNoise_2D vn;
    private MapPreset mp;

    public MapGenerator(MapPreset mp) {
        this.mp = mp;
        this.vn = new ValueNoise_2D(mp.getPresetMap().length, mp.getPresetMap()[0].length);

        vn.calculate();
    }

    public Field[][] getMap() {
        Field[][] out = new Field[mp.getPresetMap().length][mp.getPresetMap()[0].length];

        for(int x = 0; x < out.length; x++) {
            for(int y = 0; y < out[0].length; y++){
                if(mp.getPresetMap()[x][y] == Field.VOID) {
                    out[x][y] = Field.VOID;
                    System.out.println(x + " " + y);
                    continue;
                }

                float f = vn.getHeightMap()[x][y];

                if(f <= 1.0f/9.0f)out[x][y] = Field.WATER;
                else if(f <= 2.0f/9.0f)out[x][y] = Field.SAND;
                else if(f <= 3.0f/9.0f)out[x][y] = Field.GRASS;
                else if(f <= 4.0f/9.0f)out[x][y] = Field.FOREST;
                else if(f <= 5.0f/9.0f)out[x][y] = Field.GRASS_ROCK;
                else if(f <= 6.0f/9.0f)out[x][y] = Field.DIRT;
                else if(f <= 7.0f/9.0f)out[x][y] = Field.DIRT_ROCK;
                else if(f <= 8.0f/9.0f)out[x][y] = Field.MARS;
                else if(f <= 9.0f/9.0f)out[x][y] = Field.STONE;
                else System.out.println(f);
            }
        }

        return out;
    }
}
