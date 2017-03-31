package client.window.view;

import client.Controller;
import client.window.View;
import client.window.Window;
import game.enums.PlayerColor;
import networking.client.ClientListener;
import networking.gamePackets.clientPackets.PacketClientKicked;
import networking.gamePackets.preGamePackets.*;
import networking.packets.Packet;

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

	private JTextArea info;
	private JButton button_backToServerConnect, button_toggleReady, button_toggleColor;

	private DynamicBackground background;
	private boolean started = false;

	public ViewGameSetup(String userName, String hostName, int port) {
		this.userName = userName;
		this.hostName = hostName;
		this.port = port;

		this.ready = new HashMap<>();
		this.color = new HashMap<>();
	}

	public ViewGameSetup(DynamicBackground background, String userName, String hostName, int port) {
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

		button_backToServerConnect = new JButton("Quit");
		button_backToServerConnect.addActionListener(e -> {controller.stopConnection(); window.updateView(new ViewServerConnect(background));});

		button_toggleReady = new JButton("Toggle Ready");
		button_toggleReady.addActionListener(e -> controller.sendPacket(new PacketPlayerReady(userName, !ready.get(userName))));

		button_toggleColor = new JButton("Toggle Color");
		button_toggleColor.addActionListener(e -> controller.sendPacket(new PacketPlayerPickColor(userName, PlayerColor.values()[(color.get(userName).ordinal()+1) % PlayerColor.values().length])));

		info = new JTextArea();
		info.setEditable(false);

		window.getPanel().setLayout(new BorderLayout());

		JPanel panel2 = new JPanel(new FlowLayout());
		window.getPanel().add(panel2, BorderLayout.PAGE_END);

		panel2.add(button_backToServerConnect);
		panel2.add(button_toggleReady);
		panel2.add(button_toggleColor);

		window.getPanel().add(info, BorderLayout.CENTER);

		if(background == null) background = new DynamicBackground();
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
	public void draw() {
		if (!started) return;

		JPanel panel = window.getPanel();

		BufferedImage buffer = background.draw(panel.getWidth(), panel.getHeight());

		Graphics g = buffer.getGraphics();

		for (Component component: panel.getComponents()) {
			g.translate(component.getX(), component.getY());
			component.update(g);
			g.translate(-component.getX(), -component.getY());
		}

		panel.getGraphics().drawImage(buffer, 0, 0, null);
	}

	private void updateInfo() {
		String info = "";
		for (String player: ready.keySet()) {
			info += String.format("%s%s - %s - %b\n", player.equals(userName) ? "->" : "", player, color.get(player).getDisplayName(), ready.get(player));
		}
		this.info.setText(info);
	}

	private void onAllPlayersReady() {
		button_toggleReady.setEnabled(false);
		button_toggleColor.setEnabled(false);
	}

	@Override
	public void onReceivePacket(Packet p) {
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
			window.updateView(new ViewGame());
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
