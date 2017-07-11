package client.window.view;

import client.game.Controller;
import client.game.ViewGame;
import client.i18n.LanguageHandler;
import client.window.Window;
import client.window.components.TextButton;
import client.window.components.TextLabel;
import game.enums.PlayerColor;
import networking.client.ClientListener;
import networking.gamePackets.clientPackets.PacketClientKicked;
import networking.gamePackets.preGamePackets.*;
import networking.packets.Packet;
import server.ServerMain;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ViewGameSetup extends View implements ClientListener {

	private Window window;
	private Controller controller;

	private String userName;
	private String hostName;
	private int port;

	private Map<String, Boolean> ready;
	private Map<String, PlayerColor> color;

	private String displayInfo;
	private TextLabel info;
	private TextButton button_backToServerConnect, button_toggleReady, button_toggleColor;

	private DynamicBackground background;
	private boolean started = false;

	private ServerMain server;

	public ViewGameSetup(DynamicBackground background, String userName, String hostName, int port) {
		this.server = null;
		this.userName = userName;
		this.hostName = hostName;
		this.port = port;

		this.background = background;
		this.ready = new HashMap<>();
		this.color = new HashMap<>();
	}

	public ViewGameSetup(ServerMain server, DynamicBackground background, String userName, String hostName, int port) {
		this.server = server;
		this.userName = userName;
		this.hostName = hostName;
		this.port = port;

		this.background = background;
		this.ready = new HashMap<>();
		this.color = new HashMap<>();
	}

	@Override
	public void init(Window window, Controller controller) {
		this.window = window;
		this.controller = controller;

		displayInfo = "";

		button_backToServerConnect = new TextButton(window, LanguageHandler.get("Quit"), e -> {
			controller.stopConnection();
			window.updateView(server == null ? new ViewServerConnect(background) : new ViewServerCreate(background));
			if (server != null) server.stop();
		});
		button_toggleReady = new TextButton(window, LanguageHandler.get("Toggle Ready"), e -> controller.sendPacket(new PacketPlayerReady(userName, !ready.get(userName))));
		button_toggleColor = new TextButton(window, LanguageHandler.get("Toggle Color"), (e -> controller.sendPacket(new PacketPlayerPickColor(userName, PlayerColor.values()[(color.get(userName).ordinal() + 1) % PlayerColor.values().length]))));

		info = new TextLabel(new TextLabel.Text() {
			@Override
			public String getText() {
				return displayInfo;
			}
		}, false);

		changeSize();

		window.getPanel().add(button_backToServerConnect);
		window.getPanel().add(button_toggleReady);
		window.getPanel().add(button_toggleColor);

		window.getPanel().add(info);

		if (background == null) background = new DynamicBackground();
		started = true;

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (started) {
					draw();
				}
			}
		}).start();

		controller.setViewPacketListener(this);
		controller.connect(userName, hostName, port);

	}

	@Override
	public void changeSize() {
		int width = window.getPanel().getWidth();
		int height = window.getPanel().getHeight();

		int elementHeight = height / 10;
		int elementWidth = elementHeight * 5;

		info.setBounds(0, 0, width, elementHeight * 2);
		button_backToServerConnect.setBounds((width - elementWidth / 2 - 5), (height - elementHeight / 2 - 5), elementWidth / 2, elementHeight / 2);
		button_toggleReady.setBounds((width - elementWidth) / 2, (height - elementHeight) / 2, elementWidth, elementHeight);
		button_toggleColor.setBounds((width - elementWidth) / 2, (height + 2 * elementHeight) / 2, elementWidth, elementHeight);
	}

	/**
	 * Draws this screen
	 */
	public void draw() {
		if (!started) return;

		JPanel panel = window.getPanel();

		BufferedImage buffer = background.draw(panel.getWidth(), panel.getHeight());

		Graphics g = buffer.getGraphics();

		for (Component component : panel.getComponents()) {
			g.translate(component.getX(), component.getY());
			component.update(g);
			g.translate(-component.getX(), -component.getY());
		}

		panel.getGraphics().drawImage(buffer, 0, 0, null);
	}

	/**
	 * Updates player overwiew
	 */
	private synchronized void updateInfo() {
		String info = "";
		for (String player : ready.keySet()) {
			info += String.format("%s%s - %s - %b\n", player.equals(userName) ? "->" : "", player, LanguageHandler.get(color.get(player).getDisplayName()), ready.get(player));
		}

		displayInfo = info;
	}

	/**
	 * Turns buttons off when all player are ready
	 */
	private void onAllPlayersReady() {
		button_toggleReady.setEnabled(false);
		button_toggleColor.setEnabled(false);
	}

	@Override
	public synchronized void onReceivePacket(Packet p) {
		if (p instanceof PacketClientKicked) {
			controller.stopConnection();
			window.updateView(new ViewErrorScreen(background, ((PacketClientKicked) p).getReason()));
		}

		if (p instanceof PacketPlayerJoined) {
			PacketPlayerJoined packet = (PacketPlayerJoined) p;
			ready.put(packet.getName(), packet.isReady());
			color.put(packet.getName(), packet.getColor());
			updateInfo();
		}

		if (p instanceof PacketPlayerQuit) {
			PacketPlayerQuit packet = (PacketPlayerQuit) p;
			ready.remove(packet.getName());
			color.remove(packet.getName());
			updateInfo();
		}

		if (p instanceof PacketPlayerPickColor) {
			PacketPlayerPickColor packet = (PacketPlayerPickColor) p;
			color.put(packet.getPlayer(), packet.getColor());
			updateInfo();
		}

		if (p instanceof PacketPlayerReady) {
			PacketPlayerReady packet = (PacketPlayerReady) p;
			ready.put(packet.getPlayer(), packet.isReady());
			updateInfo();
		}

		if (p instanceof PacketAllPlayersReady) {
			onAllPlayersReady();
		}

		if (p instanceof PacketGameBegin) {
			window.updateView(new ViewGame(server));
		}
	}

	@Override
	public void onLeave() {
		if (window.isCurrentView(this)) {
			controller.stopConnection();
			window.updateView(new ViewErrorScreen(background, "Connection lost!"));
		}
	}

	@Override
	public void stop() {
		started = false;
	}
}
