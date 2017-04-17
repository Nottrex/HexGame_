package game.map.presets;

import game.enums.Field;

public class OvalPreset implements MapPreset{

	private int width, height;
	public OvalPreset(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public Field[][] getPresetMap() {
		Field[][] out = new Field[width][height];

		float a = width / 2;
		float b = height/2;
		for(int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[0].length; y++) {
				float x2 = x - width/2;
				float y2 = y - height/2;
				if(!(((x2*x2)/(a*a) + (y2*y2)/(b*b)) <= 1.0f)) out[x][y] = Field.VOID;
			}
		}

		return out;
	}
}
