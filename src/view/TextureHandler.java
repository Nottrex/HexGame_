package view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class TextureHandler {
	private TextureHandler() {}
	
	public static Map<String, BufferedImage> textures_png;
	
	static {
		textures_png = new HashMap<>();
	}

	public static void loadImagePng(String textureName, String fileName) {
		try {
			textures_png.put(textureName, ImageIO.read(ClassLoader.getSystemResource("res/textures/" + fileName + ".png")));
		} catch (IOException e) {
			System.err.println("Error loading texture: " + textureName);
		}
	}

	public static void unloadImagePng(String textureName) {
		textures_png.remove(textureName);
	}

	public static BufferedImage getImagePng(String textureName) {
		return textures_png.get(textureName);		
	}
}
