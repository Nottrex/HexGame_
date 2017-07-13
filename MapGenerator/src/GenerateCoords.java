import java.io.*;
import java.util.Scanner;

public class GenerateCoords {

	public static void main(String[] args) throws IOException {
		String filePath = "GenerateCoords\\src\\amCharts.pixelMap.svg";

		//Count the amount of lines
		FileReader fr = new FileReader(filePath);
		Scanner br = new Scanner(fr);
		int lineAmount = 0;
		while (br.hasNextLine()) {
			lineAmount++;
			br.nextLine();
		}
		lineAmount = lineAmount - 28;
		br.close();

		//Filter all the coordinates
		fr = new FileReader(filePath);
		br = new Scanner(fr);
		double[] coordsX = new double[lineAmount / 3];
		double[] coordsY = new double[lineAmount / 3];
		for (int i = 0; i < 11; i++) {
			br.nextLine();
		}
		for (int i = 0; i < lineAmount / 3; i++) {
			String line = br.nextLine();
			coordsX[i] = Double.parseDouble(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
			coordsY[i] = Double.parseDouble(line.substring(line.indexOf(",") + 1, line.indexOf(")")));
			br.nextLine();
			br.nextLine();
		}
		br.close();

		//Analyze gathered data
		double smallestX = Double.MAX_VALUE;
		double smallestY = Double.MAX_VALUE;
		double largestX = Double.MIN_VALUE;
		double largestY = Double.MIN_VALUE;
		for (int i = 0; i < coordsX.length; i++) {
			if (coordsX[i] < smallestX) smallestX = coordsX[i];
			if (coordsY[i] < smallestY) smallestY = coordsY[i];
			if (coordsX[i] > largestX) largestX = coordsX[i];
			if (coordsY[i] > largestY) largestY = coordsY[i];
		}
		double distanceX = Double.MAX_VALUE;
		double distanceY = Double.MAX_VALUE;
		for (int i = 0; i < coordsX.length; i++) {
			for (int j = 0; j < coordsX.length; j++) {
				if (coordsY[i] == coordsY[j] && coordsX[i] != coordsX[j] && Math.abs(coordsX[i] - coordsX[j]) < distanceX) {
					distanceX = Math.abs(coordsX[i] - coordsX[j]);
				}
				if (coordsX[i] == coordsX[j] && coordsY[i] != coordsY[j] && Math.abs(coordsY[i] - coordsY[j]) < distanceY) {
					distanceY = Math.abs(coordsY[i] - coordsY[j]);
				}
			}
		}
		distanceY = distanceY / 2;
		int amountX = (int) (Math.ceil((largestX - smallestX) / distanceX));
		int amountY = (int) (Math.ceil((largestY - smallestY) / distanceY));

		//Plot coordinates onto map
		boolean[][] map = new boolean[amountX][amountY];
		for (int i = 0; i < coordsX.length; i++) {
			map[(int) ((coordsX[i] - smallestX) / distanceX)][(int) ((coordsY[i] - smallestY) / distanceY)] = true;
		}
		String[] output = new String[amountY + 5];
		output[0] = (amountX + amountY / 2) + " " + amountY;
		for (int i = 0; i < amountY; i++) {
			output[i+1] = "";
			for (int j = 0; j < amountX; j++) {
				if (map[j][i]) output[i + 1] = output[i + 1] + "1";
				else output[i + 1] = output[i + 1] + "0";
			}
		}
		output[output.length - 4] = 1 + " " + 1; //TODO spawnpoints
		output[output.length - 3] = 1 + " " + 2;
		output[output.length - 2] = 1 + " " + 3;
		output[output.length - 1] = 1 + " " + 4;

		//Straighten map
		for (int i = 1; i < 1 + amountY; i++) {
			String end = "";
			String front = "";
			for (int j = 0; j < i / 2; j++) {
				front = front + "0";
			}
			for (int j = 0; j < (int) ((amountY - i) / 2.0f + 0.5f); j++) { //TODO better term
				end = end + "0";
			}
			output[i] = front + output[i] + end;
		}
		writeFile(output);
	}

	private static void writeFile(String[] lines) throws IOException {
		FileWriter fw = new FileWriter("src\\res\\files\\maps\\mapOutput.txt");
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
