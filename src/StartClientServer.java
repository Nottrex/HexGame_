import client.window.Window;
import client.window.view.ViewGameSetup;
import server.ServerMain;

public class StartClientServer {
	public static void main(String[] args) {
		Window window = new Window();
		window.updateView(new ViewGameSetup(new ServerMain(25565), null, "1", "localhost", 25565));
	}
}
