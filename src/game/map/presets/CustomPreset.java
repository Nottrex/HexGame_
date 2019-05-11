package game.map.presets;

import game.Location;
import game.enums.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomPreset implements MapPreset {

	private int width, height;
	private String mapContent;

	public CustomPreset(String mapContent) {
		this.mapContent = mapContent;
		Scanner br = new Scanner(mapContent);

		this.width = Integer.parseInt(br.next());
		this.height = Integer.parseInt(br.next());
		br.close();
	}

	@Override
	public Field[][] getPresetMap() {
		Field[][] out = new Field[width][height];
		Scanner br = new Scanner(mapContent);

		br.nextLine();

		for (int y = 0; y < height; y++) {
			String line = br.nextLine();
			for (int x = 0; x < width; x++) {
				char character = line.charAt(x);
				if (character == '0') out[x][y] = Field.WATER;
				else if (character == '1') out[x][y] = Field.FOREST;
				else if (character == '2') out[x][y] = Field.GRASS;
				else if (character == '3') out[x][y] = Field.GRASS_ROCK;
				else if (character == '4') out[x][y] = Field.DIRT;
				else if (character == '5') out[x][y] = Field.DIRT_ROCK;
				else if (character == '6') out[x][y] = Field.SAND;
				else if (character == '7') out[x][y] = Field.SNOW;
				else if (character == '8') out[x][y] = Field.STONE;
				else if (character == 'V') out[x][y] = Field.VOID;
			}
		}
		br.close();

		return out;
	}

	@Override
	public List<Location> getSpawnPoints() {
		List<Location> locations = new ArrayList<>();

		Scanner br = new Scanner(mapContent);
		for (int i = 0; i < height + 1; i++) {
			br.nextLine();
		}

		locations.add(new Location(Integer.parseInt(br.next()), Integer.parseInt(br.next())));
		locations.add(new Location(Integer.parseInt(br.next()), Integer.parseInt(br.next())));
		locations.add(new Location(Integer.parseInt(br.next()), Integer.parseInt(br.next())));
		locations.add(new Location(Integer.parseInt(br.next()), Integer.parseInt(br.next())));
		br.close();

		return locations;
	}
}
