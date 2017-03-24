package game.map.presets;

import game.enums.Field;

public class SquarePreset implements  MapPreset{

    private int width, height;
    public SquarePreset(int width, int height) {
        this.width = width;
        this.height = height;
    }


    @Override
    public Field[][] getPresetMap() {
        return new Field[width][height];
    }
}
