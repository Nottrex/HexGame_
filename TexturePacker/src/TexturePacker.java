import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.List;

public class TexturePacker {

	public static void main(String[] args) {

		TexturePacker tp = new TexturePacker();
		Scanner s = new Scanner(System.in);

		String input = s.nextLine();
		while(!input.equals("exit")) {
			if(input.startsWith("add")) {
				String[] content = input.split(" ");
				if(content.length == 3) {
					tp.addImage(content[1], Util.load(content[2]));
					System.out.println("There are now " + tp.getSize() + " images");
				}else System.out.println("Usage: add [name] [file path]");
			}else if(input.startsWith("remove")) {
				if(input.split(" ").length == 2) {
					tp.removeImage(input.split(" ")[1]);
					System.out.println("There are now " + tp.getSize() + " images");
				} else System.out.println("Usage: remove [name]");
			}else if(input.startsWith("pack")) {
				tp.pack();
				System.out.println("Finished packing");
			}else if(input.startsWith("save")) {
				if(input.split(" ").length != 2) {
					System.out.println("Usage: save [filename]");
				}else {
					tp.save(input.split(" ")[1]);
					System.out.println("Finished saving");
				}
			}else if(input.startsWith("help")) {
				System.out.println("add [name] [file path] - adds a image");
				System.out.println("remove [name] - removes image");
				System.out.println("pack - puts images together");
				System.out.println("save [filename] - saves the .png and .text");
				System.out.println("exit - exits the program");
			}

			input = s.nextLine();
		}
	}

	private Map<String, BufferedImage> content;
	private Map<Location, BufferedImage> locations;
	private BufferedImage packedContent;

	public TexturePacker() {

		content = new HashMap<>();

	}

	public boolean addImage(String name, BufferedImage image) {
		if(image != null) {
			content.put(name, image);
			return true;
		}else return false;
	}

	public boolean removeImage(String name) {
		if(content.keySet().contains(name)) {
			return content.remove(name, content.get(name));
		}else return false;
	}

	private int[] optimum() {
		int max = (int) Math.ceil(Math.sqrt(content.size()));

		int width = 1;
		int height = 1;
		int leftover = Integer.MAX_VALUE;

		for(int w = 1; w <= max; w++) {
			for(int h = 1; h <= max; h++) {

				int left = (w * h) - getSize();
				if(left < leftover && left >= 0) {
					width = w;
					height = h;
					leftover = left;
				}
			}
		}

		return new int[]{width, height};
	}

	public void pack() {
		int[] i = optimum();
		pack(i[0], i[1]);
	}

	public void pack(int rows, int columns) {

		locations = new HashMap<>();
		Location latest = new Location(0, 0);

		List<String> name = new ArrayList<>();
		name.addAll(content.keySet());

		int width = columns * content.get(name.get(0)).getWidth();
		int height = rows * content.get(name.get(0)).getHeight();

		int w = 0, h = 0;

		while(Math.pow(2, w) < w) w++;
		while(Math.pow(2, h) < h) w++;

		packedContent = new BufferedImage((int) Math.pow(2, w), (int) Math.pow(2, h), BufferedImage.TYPE_INT_ARGB);
		Graphics g = packedContent.getGraphics();

		for(String s: content.keySet()) {
			BufferedImage i = content.get(s);

			int x, y = latest.y;
			if(latest.x + i.getWidth() <= width) {
				x = latest.x;
			}else {
				x = 0;
				y += i.getHeight();
			}

			g.drawImage(i, x, y,null);

			locations.put(new Location(x, y), i);
			latest = new Location(x + i.getWidth(), y);
		}
	}

	public void save(String fileName) {
		try {
			if(!fileName.endsWith(".png"))fileName += ".png";
			ImageIO.write(packedContent, "png", new File(fileName));
		}catch(Exception e) {}

		File textFile = new File(fileName.split(".png")[0] + ".text");
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(textFile));
			w.write(getSize() + "\n");
			for(Location l: locations.keySet()) {
				BufferedImage i = locations.get(l);
				String name = "";

				for(String s: content.keySet()) if(content.get(s).equals(i)) name = s;

				w.write(name + " " + l.x + " " + l.y + " " + i.getWidth() + " " + i.getHeight() + "\n");
			}

			w.close();
		} catch(Exception e) {}
	}

	public int getSize() {
		return content.size();
	}
}