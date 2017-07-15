package game.map;

import game.Location;
import game.enums.Direction;
import game.enums.Field;
import game.map.presets.MapPreset;

import java.util.List;

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
		Field[][] presetMap = mp.getPresetMap();
		Field[][] out = new Field[presetMap.length][presetMap[0].length];
		float[][] heightMap = vn.getHeightMap();

		float max = -1;
		for (int x = 0; x < heightMap.length; x++) {
			for (int y = 0; y < heightMap[0].length; y++) {
				if (presetMap[x][y] != null) continue;
				float d = distance(new Location(x, y), new Location(presetMap.length / 2, presetMap[0].length / 2));
				if (d > max) max = d;
			}
		}
		float highest = -1;
		for (int x = 0; x < heightMap.length; x++) {
			for (int y = 0; y < heightMap[0].length; y++) {
				if (presetMap[x][y] != null) continue;
				float r = raise(x, y, presetMap.length, presetMap[0].length, max);

				heightMap[x][y] *= r;
				if (heightMap[x][y] > highest) highest = heightMap[x][y];
			}
		}

		for (int x = 0; x < heightMap.length; x++) {
			for (int y = 0; y < heightMap[0].length; y++) {

				heightMap[x][y] /= highest;
			}
		}

		for (int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[0].length; y++) {
				if (presetMap[x][y] != null) {
					out[x][y] = presetMap[x][y];
					continue;
				}

				float f = heightMap[x][y];
				if (f <= 1.3 / 9.0f) out[x][y] = Field.WATER;
				else if (f <= 1.75f / 9.0f) out[x][y] = Field.SAND;
				else if (f <= 2.85f / 9.0f) out[x][y] = Field.GRASS;
				else if (f <= 3.8f / 9.0f) out[x][y] = Field.FOREST;
				else if (f <= 4.4f / 9.0f) out[x][y] = Field.GRASS_ROCK;
				else if (f <= 5.3f / 9.0f) out[x][y] = Field.DIRT;
				else if (f <= 6.4f / 9.0f) out[x][y] = Field.DIRT_ROCK;
				else if (f <= 7.5f / 9.0f) out[x][y] = Field.STONE;
				else if (f <= 9.0f / 9.0f) out[x][y] = Field.SNOW;
			}
		}

		for (Location l : mp.getSpawnPoints()) {
			out[l.x][l.y] = Field.GRASS;

			for (Direction d : Direction.values()) {
				Location l2 = d.applyMovement(l);
				out[l2.x][l2.y] = Field.GRASS;
			}
		}

		return out;
	}

	private float raise(int x, int y, int width, int heigth, float max) {
		float d = distance(new Location(x, y), new Location(width / 2, heigth / 2)) / max;

		if (d < 0.5) return 1 - 1.5f * d;
		else return -0.16f + 0.82f * d;

		// return (-3 * (float)Math.pow(d*1.5f, 3) + 9 * (float)Math.pow(d*1.5f, 2) - (29.0f/4.0f)* d * 1.5f + 15.0f/8.0f) / 1.5f;
	}

	private float distance(Location a, Location b) {
		return (float) Math.sqrt(Math.pow(a.x - a.y / 2.0f - (b.x - b.y / 2.0f), 2) + Math.pow(a.y - b.y, 2));
	}
}