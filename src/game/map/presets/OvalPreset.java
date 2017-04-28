package game.map.presets;

import game.Location;
import game.enums.Field;

import java.util.ArrayList;
import java.util.List;

public class OvalPreset implements MapPreset{

	private int width, height;
	public OvalPreset(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public Field[][] getPresetMap() {
		Field[][] out = new Field[width + height][height];

		for(int x = 0; x < out.length; x++) {
			for(int y = 0; y < out[0].length; y++) {
				out[x][y] = Field.VOID;
			}
		}

		float a = width/2;
		float b = height/2;
		for(int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				float x2 = x - width/2;
				float y2 = y - height/2;
				if((((x2*x2)/(a*a) + (y2*y2)/(b*b)) <= 1.0f)) {
					out[x + y/2][y] = null;
				}
			}
		}

		return out;
	}

	@Override
	public List<Location> getSpawnPoints() {
		List<Location> locations = new ArrayList<>();
		locations.add(new Location(width/2 + 1, 2));
		locations.add(new Location(width/2 + height/2, height - 2));
		locations.add(new Location(height/4, height/2));		//
		locations.add(new Location(width + height/4 - 1, height/2));
		return locations;
	}
}
