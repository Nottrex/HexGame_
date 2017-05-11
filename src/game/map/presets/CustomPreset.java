package game.map.presets;

import game.Location;
import game.enums.Field;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

public class CustomPreset implements MapPreset{

    private int width, height;
    private String mapName;

    public CustomPreset(String mapName) {
        try {
            this.mapName = mapName;
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/src/res/maps/" + mapName + ".txt");
            BufferedReader br = new BufferedReader(reader);

            this.width = Integer.parseInt(br.readLine());
            this.height = Integer.parseInt(br.readLine());
            br.close();

        } catch (IOException e) {
            System.err.println("Error loading map: " + mapName);
            System.exit(-1);
        }
    }
    
    @Override
    public Field[][] getPresetMap() {
        Field[][] out = new Field[width][height];

        try {
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/src/res/maps/" + mapName + ".txt");
            BufferedReader br = new BufferedReader(reader);

            for (int x = 0; x < width; x++) {
                String line = br.readLine();
                for (int y = 0; y < height; y++) {
                    char character = line.charAt(y);
                    if (character == '0') out[x][y] = Field.VOID;
                }
            }
            br.close();

        } catch (IOException e) {
            System.err.println("Error loading map: " + mapName);
            System.exit(-1);
        }

        return out;
    }

    @Override
    public List<Location> getSpawnPoints() {
        List<Location> locations = new ArrayList<>();

        locations.add(new Location(1, 1));
        locations.add(new Location(2, 2));
        locations.add(new Location(3, 3));
        locations.add(new Location(4, 4));

        return locations;
    }
}
