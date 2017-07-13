import java.io.*;
import java.util.Scanner;

public class CalculateCoords {
	public static void main(String[] args) throws IOException {
		FileReader fr = new FileReader("C:\\Users\\Yannick Lehmann\\Desktop\\HexGame_\\MapGenerator\\src\\filteredCoords.txt");
		Scanner br = new Scanner(fr);
		int lineAmount = 0;
		while (br.hasNextLine()) {
			lineAmount++;
			br.nextLine();
		}
		br.close();
		fr = new FileReader("C:\\Users\\Yannick Lehmann\\Desktop\\HexGame_\\MapGenerator\\src\\filteredCoords.txt");
		br = new Scanner(fr);
		double[] coordsX = new double[lineAmount];
		double[] coordsY = new double[lineAmount];
		double smallestX = Double.MAX_VALUE;
		double smallestY = Double.MAX_VALUE;
		double largestX = Double.MIN_VALUE;
		double largestY = Double.MIN_VALUE;
		for (int i = 0; i < lineAmount; i++) {
			String line = br.nextLine();
			coordsX[i] = Double.parseDouble(line.substring(0, line.indexOf(",")));
			coordsY[i] = Double.parseDouble(line.substring(line.indexOf(",") + 1));
			if (coordsX[i] < smallestX) {
				smallestX = coordsX[i];
			}
			if (coordsY[i] < smallestY) {
				smallestY = coordsY[i];
			}
			if (coordsX[i] > largestX) {
				largestX = coordsX[i];
			}
			if (coordsY[i] > largestY) {
				largestY = coordsY[i];
			}
		}
		double smallestXdistance = Double.MAX_VALUE;
		double smallestYdistance = Double.MAX_VALUE;
		for (int i = 0; i < lineAmount; i++) {
			for (int j = 0; j < lineAmount; j++) {
				if (coordsX[i] == coordsX[j] && coordsY[i] != coordsY[j]) {
					if (Math.abs(coordsY[i] - coordsY[j]) < smallestYdistance) {
						smallestYdistance = Math.abs(coordsY[i] - coordsY[j]);
					}
				}
				if (coordsY[i] == coordsY[j] && coordsX[i] != coordsX[j]) {
					if (Math.abs(coordsX[i] - coordsX[j]) < smallestXdistance) {
						smallestXdistance = Math.abs(coordsX[i] - coordsX[j]);
					}
				}
			}
		}
		smallestYdistance = smallestYdistance / 2;
		int amountX = (int) (Math.ceil((largestX - smallestX) / smallestXdistance));
		int amountY = (int) (Math.ceil((largestY - smallestY) / smallestYdistance));
		boolean[][] coords = new boolean[amountX][amountY];
		for (int i = 0; i < lineAmount; i++) {
			coords[(int) ((coordsX[i] - smallestX) / smallestXdistance)][(int) ((coordsY[i] - smallestY) / smallestYdistance)] = true;
		}
		System.out.println(smallestX + " | " + smallestY);
		System.out.println(largestX + " | " + largestY);
		System.out.println(smallestXdistance + " | " + smallestYdistance);
		System.out.println(amountX + " | " + amountY);
		br.close();
		String[] output = new String[amountY + 5];
		output[0] = amountX + " " + amountY;
		for (int i = 0; i < amountY; i++) {
			for (int j = 0; j < amountX; j++) {
				if (j == 0) output[i+1] = "";
				if (coords[j][i]) {
					output[i+1] = output[i+1] + "1";
				} else {
					output[i+1] = output[i+1] + "0";
				}
			}
		}
		output[output.length - 4] = amountX + " " + amountY;
		output[output.length - 3] = amountX + " " + amountY;
		output[output.length - 2] = amountX + " " + amountY;
		output[output.length - 1] = amountX + " " + amountY;
		for (int i = 1; i < 1+amountY; i++) {
			String back = "";
			String front = "";
			for (int j = 0; j < i/2; j++) {
				front = front + "0";
			}
			for (int j = 0; j < (int)((amountY - i)/2.0f + 0.5f); j++) {
				back = back + "0";
			}
			output[i] = front + output[i] + back;
		}
		output[0] = "" + (amountX + amountY/2) + " " + amountY;
		writeFile(output);
	}

	public static void writeFile(String[] lines) throws IOException {
		FileWriter fw = new FileWriter("C:\\Users\\Yannick Lehmann\\Desktop\\HexGame_\\MapGenerator\\src\\germany.txt");
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
