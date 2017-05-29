package game.map.presets;

import game.Location;
import game.enums.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomPreset implements MapPreset{

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

        Scanner br = new Scanner(mapContent);
        for (int i = 0; i < width+1; i++) {
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
