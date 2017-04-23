import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Util {

	public static BufferedImage load(String file) {
		try {
			return ImageIO.read(ClassLoader.getSystemResource(file));
		}catch(Exception e) {
			return null;
		}
	}
}