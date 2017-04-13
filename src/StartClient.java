import client.window.Window;
import client.window.view.ViewGameSetup;

public class StartClient {
	public static void main(String[] args) {
		Window window = new Window();
		window.updateView(new ViewGameSetup(null, "2", "localhost", 25565));
	}
}
