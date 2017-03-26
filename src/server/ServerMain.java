package server;

import game.Game;
import game.enums.PlayerColor;
import networking.ServerState;
import networking.gamePackets.clientPackets.PacketClientInfo;
import networking.gamePackets.clientPackets.PacketClientKicked;
import networking.gamePackets.clientPackets.PacketServerInfo;
import networking.gamePackets.preGamePackets.PacketPlayerJoined;
import networking.gamePackets.preGamePackets.PacketPlayerPickColor;
import networking.gamePackets.preGamePackets.PacketPlayerQuit;
import networking.gamePackets.preGamePackets.PacketPlayerReady;
import networking.packets.Packet;
import networking.server.Server;
import networking.server.ServerListener;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerMain implements ServerListener {

	private Server server;
	private ServerState serverState;

	private Map<Socket, String> players;
	private Map<String, Boolean> playerReady;
	private Map<String, PlayerColor> playerColor;

	public ServerMain() {
		serverState = ServerState.WAITING_FOR_PLAYERS;

		players = new HashMap<>();
		playerReady = new HashMap<>();

		Scanner s = new Scanner(System.in);

		System.out.print("Port: ");

		int port = -1;
		while (port == -1) {
			try {
				port = Integer.valueOf(s.nextLine());
			} catch(Exception e) {
				System.out.print("The port must be a number\nPort: ");
			}
		}

		server = new Server(port, this);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					String next = s.nextLine();
					if (next.equalsIgnoreCase("stop")) {
						server.close();
						System.exit(0);
					}
				}
			}
		}).start();
	}

	public void playerQuit(String player) {
		Socket s = getPlayerSocket(player);

		players.keySet().stream()
				.filter(s2 -> s2 != s)
				.forEach(s2 -> server.sendPacket(s2, new PacketPlayerQuit(player)));

		players.remove(s);
		if (playerReady.containsKey(s)) playerReady.remove(s);
	}

	public void playerJoined(String player) {
		Socket s = getPlayerSocket(player);

		players.keySet().stream()
				.filter(s2 -> s2 != s)
				.forEach(s2 -> server.sendPacket(s, new PacketPlayerJoined(players.get(s2))));

		playerReady.keySet().stream()
				.forEach(s2 -> server.sendPacket(s, new PacketPlayerReady(s2, playerReady.get(s2))));

		playerColor.keySet().stream()
				.forEach(s2 -> server.sendPacket(s, new PacketPlayerPickColor(s2, playerColor.get(s2))));

		playerReady.put(player, false);
		playerColor.put(player, PlayerColor.BLUE);

		players.keySet().stream()
				.forEach(s2 -> server.sendPacket(s2, new PacketPlayerJoined(player)));

		players.keySet().stream()
				.forEach(s2 -> server.sendPacket(s2, new PacketPlayerPickColor(player, playerColor.get(player))));
		players.keySet().stream()
				.forEach(s2 -> server.sendPacket(s2, new PacketPlayerReady(player, playerReady.get(player))));

	}

	public void startGame() {
		System.out.println("Start game!");
	}

	public void onReceivePacket(Socket s, Packet p) {
		if (p instanceof PacketPlayerReady) {
			PacketPlayerReady packet = (PacketPlayerReady) p;
			if (!players.containsKey(s)) return;

			if (players.get(s).equals(packet.getPlayer())) {
				playerReady.put(packet.getPlayer(), packet.isReady());

				players.keySet().stream()
						.filter(s2 -> s2 != s)
						.forEach(s2 -> server.sendPacket(s2, new PacketPlayerReady(packet.getPlayer(), packet.isReady())));

				if (!playerReady.values().contains(false)) {
					serverState = ServerState.INGAME;
					new Thread(() -> startGame()).start();
				}
			}
		}

		if (p instanceof PacketPlayerPickColor) {
			PacketPlayerPickColor packet = (PacketPlayerPickColor) p;
			if (!players.containsKey(s)) return;

			if (players.get(s).equals(packet.getPlayer())) {
				playerColor.put(packet.getPlayer(), packet.getColor());

				players.keySet().stream()
						.filter(s2 -> s2 != s)
						.forEach(s2 -> server.sendPacket(s2, new PacketPlayerPickColor(packet.getPlayer(), packet.getColor())));
			}
		}

		if (p instanceof PacketClientInfo) {
			PacketClientInfo packet = (PacketClientInfo) p;

			if (players.containsKey(s)) return;

			if (!packet.getClientVersion().equals(Game.VERSION)) {
				server.sendPacket(s, new PacketClientKicked(String.format("Client version is not equal to server version %s != %s", packet.getClientVersion(), Game.VERSION)));
				server.kickClient(s);
				return;
			}

			if (players.containsValue(packet.getClientName())) {
				server.sendPacket(s, new PacketClientKicked("Name already in use: " + packet.getClientName()));
				server.kickClient(s);
				return;
			}

			if (serverState != ServerState.WAITING_FOR_PLAYERS) {
				server.sendPacket(s, new PacketClientKicked("The game has already started"));
				server.kickClient(s);
				return;
			}

			if (players.size() >= PlayerColor.values().length) {
				server.sendPacket(s, new PacketClientKicked("The game is full"));
				server.kickClient(s);
				return;
			}

			players.put(s, packet.getClientName());
			playerJoined(packet.getClientName());
		}
	}

	public Socket getPlayerSocket(String player) {
		for (Socket s: players.keySet()) {
			if (players.get(s).equals(player)) return s;
 		}
		return null;
	}

	public void onClientLeave(Socket s) {
		System.out.println("Disconnected: " + s.getInetAddress());

		if (players.containsKey(s)) {
			playerQuit(players.get(s));
		}
	}

	public void onClientJoin(Socket s) {
		System.out.println("Connected: " + s.getInetAddress());

		server.sendPacket(s, new PacketServerInfo(Game.VERSION, serverState));
	}

	public static void main(String[] args) {
		new ServerMain();
	}
}
