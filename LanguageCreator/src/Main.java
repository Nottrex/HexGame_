import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static void main(String [] arhs) {

		String[] calles = new String[]{"Tank", "Cavalry", "Infantry", "Artillery", "Water", "Forest",
				"Dirt", "Snow", "Sand", "Stone", "Grass_Rock", "Dirt_Rock", "Grass", "Void", "Red",
				"Green", "Yellow", "Blue", "Join Game", "Create Game", "Connect", "Back to Mainemnu",
				"Toggle Ready", "Toggle Color", "Quit", "Exit Game", "Accept", "Cancel", "Square", "Oval",
				"Hexagonal", "Effects Volume", "Music Volume", "Use AA", "Width", "Height", "Round", "Costs",
				"Movementrange", "Attackrange", "Custom", "Advanced Settings", "Options", "Back", "Add Player"};

		Map<String, String> data = new HashMap<>();
		Scanner s = new Scanner(System.in);

		for(String st: calles) {
			System.out.println(String.format("What is your word for '%s'", st));
			data.put(st, s.nextLine());
		}
		System.out.println("How is your language called?");

		DumperOptions op = new DumperOptions();
		op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(op);
		try{
			yaml.dump(data, new FileWriter(new File(s.nextLine() + ".yml")));
		}catch(Exception e){}
		s.close();



	}
}