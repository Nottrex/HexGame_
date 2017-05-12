package client;

import java.io.*;

public class FileHandler {

	public static String loadFile(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("res/files/" + fileName)));

			StringBuilder source = new StringBuilder();
			String line;
			while((line=reader.readLine()) != null) {
				source.append(line).append("\n");
			}
			reader.close();

			return source.toString();
		} catch (IOException e) {
			System.err.println("Error reading file: " + fileName);
			System.exit(-1);
		}
		return null;
	}
}
