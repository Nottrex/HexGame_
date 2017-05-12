package game.map.presets;

import client.FileHandler;
import game.Location;
import game.enums.Field;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.Scanner;

public class CustomPreset implements MapPreset{

    private int width, height;
    private String mapName;

    public CustomPreset(String mapName) {
        this.mapName = mapName;
        Scanner br = new Scanner(FileHandler.loadFile("maps/" + mapName + ".txt"));

        this.width = Integer.parseInt(br.nextLine());
        this.height = Integer.parseInt(br.nextLine());
        br.close();
    }
    
    @Override
    public Field[][] getPresetMap() {
        Field[][] out = new Field[width][height];
        Scanner br = new Scanner(FileHandler.loadFile("maps/" + mapName + ".txt"));

        br.nextLine();
        br.nextLine();

        for (int x = 0; x < width; x++) {
            String line = br.nextLine();
            for (int y = 0; y < height; y++) {
                char character = line.charAt(y);
                if (character == '0') out[x][y] = Field.VOID;
            }
        }
        br.close();

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
