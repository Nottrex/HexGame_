import java.io.*;
import java.util.Scanner;

public class FilterCoords {

	public static void main(String[] args) throws IOException {
		FileReader fr = new FileReader("C:\\Users\\Yannick Lehmann\\Desktop\\HexGame_\\MapGenerator\\src\\amCharts.pixelMap (2).svg");
		Scanner br = new Scanner(fr);
		int lineAmount = 0;
		while (br.hasNextLine()) {
			lineAmount++;
			br.nextLine();
		}
		br.close();
		fr = new FileReader("C:\\Users\\Yannick Lehmann\\Desktop\\HexGame_\\MapGenerator\\src\\amCharts.pixelMap (2).svg");
		br = new Scanner(fr);
		String[] lines = new String[lineAmount / 3];
		for (int i = 0; i < (lineAmount / 3); i++) {
			lines[i] = br.nextLine();
			br.nextLine();
			br.nextLine();
		}
		for (int i = 0; i < (lineAmount / 3); i++) {
			lines[i] = lines[i].substring(lines[i].indexOf("(") + 1, lines[i].indexOf(")"));
		}
		br.close();
		writeFile(lines);
	}

	public static void writeFile(String[] lines) throws IOException {
		FileWriter fw = new FileWriter("C:\\Users\\Yannick Lehmann\\Desktop\\HexGame_\\MapGenerator\\src\\filteredCoords.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < lines.length - 1; i++) {
			bw.write(lines[i]);
			bw.newLine();
		}
		bw.write(lines[lines.length - 1]);
		bw.flush();
		bw.close();
	}
}
