import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static void main(String [] arhs) {

		Map<String, String> data = new HashMap<>();
		Scanner s = new Scanner(System.in);

		System.out.println("What is your word for 'Tank'");
		data.put("Tank", s.nextLine());

		System.out.println("What is your word for 'Cavalry'");
		data.put("Cavalry", s.nextLine());

		System.out.println("What is your word for 'Infantry'");
		data.put("Infantry", s.nextLine());

		System.out.println("What is your word for 'Artillery'");
		data.put("Artillery", s.nextLine());

		System.out.println("What is your word for 'Water'");
		data.put("Water", s.nextLine());

		System.out.println("What is your word for 'Forest'");
		data.put("Forest", s.nextLine());

		System.out.println("What is your word for 'Dirt'");
		data.put("Dirt", s.nextLine());

		System.out.println("What is your word for 'Snow'");
		data.put("Snow", s.nextLine());

		System.out.println("What is your word for 'Sand'");
		data.put("Sand", s.nextLine());

		System.out.println("What is your word for 'Stone'");
		data.put("Stone", s.nextLine());

		System.out.println("What is your word for 'Grass_Rock'");
		data.put("Grass_Rock", s.nextLine());

		System.out.println("What is your word for 'Dirt_Rock'");
		data.put("Dirt_Rock", s.nextLine());

		System.out.println("What is your word for 'Grass'");
		data.put("Grass", s.nextLine());

		System.out.println("What is your word for 'Void'");
		data.put("Void", s.nextLine());

		System.out.println("What is your word for 'Red'");
		data.put("Red", s.nextLine());

		System.out.println("What is your word for 'Green'");
		data.put("Green", s.nextLine());

		System.out.println("What is your word for 'Yellow'");
		data.put("Yellow", s.nextLine());

		System.out.println("What is your word for 'Blue'");
		data.put("Blue", s.nextLine());

		System.out.println("What is your word for 'Join Game'");
		data.put("Join Game", s.nextLine());

		System.out.println("What is your word for 'Create Game'");
		data.put("Create Game", s.nextLine());

		System.out.println("What is your word for 'Connect'");
		data.put("Connect", s.nextLine());

		System.out.println("What is your word for 'Back to Mainmenu'");
		data.put("Back to Mainmenu", s.nextLine());

		System.out.println("What is your word for 'Toggle Ready'");
		data.put("Toggle Ready", s.nextLine());

		System.out.println("What is your word for 'Toggle Color'");
		data.put("Toggle Color", s.nextLine());

		System.out.println("What is your word for 'Quit'");
		data.put("Quit", s.nextLine());

		System.out.println("What is your word for 'Exit Game'");
		data.put("Exit Game", s.nextLine());

		System.out.println("What is your word for 'Accept'");
		data.put("Accept", s.nextLine());

		System.out.println("What is your word for 'Cancel'");
		data.put("Cancel", s.nextLine());

		System.out.println("What is your word for 'Square'");
		data.put("Square", s.nextLine());

		System.out.println("What is your word for 'Oval'");
		data.put("Oval", s.nextLine());

		System.out.println("What is your word for 'Hexgonal'");
		data.put("Hexgonal", s.nextLine());

		System.out.println("What is your word for 'Effects Volume'");
		data.put("Effects Volume", s.nextLine());

		System.out.println("What is your word for 'Music Volume'");
		data.put("Music Volume", s.nextLine());

		System.out.println("What is your word for 'Use AA'");
		data.put("Use AA", s.nextLine());

		System.out.println("What is your word for 'Width'");
		data.put("Width", s.nextLine());

		System.out.println("What is your word for 'Height'");
		data.put("Height", s.nextLine());

		System.out.println("What is your word for 'Round'");
		data.put("Round", s.nextLine());

		System.out.println("What is your word for 'Costs'");
		data.put("Costs", s.nextLine());

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