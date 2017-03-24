package game.map.presets;

import game.enums.Field;

public class HexPreset implements MapPreset{

    private int width, height;
    public HexPreset(int width, int height) {
        this.width = width;
        this.height = height;
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
}
