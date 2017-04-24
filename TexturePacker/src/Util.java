import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Util {

	public static BufferedImage load(String file) {
		try {
			return ImageIO.read(ClassLoader.getSystemResource(file));
		}catch(Exception e) {
			try {
				return ImageIO.read(new File(file));
			}catch(Exception e2) {
				return null;
			}
		}
	}
}