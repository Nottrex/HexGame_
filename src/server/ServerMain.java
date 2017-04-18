package server;

import game.Game;
import game.Unit;
import game.enums.PlayerColor;
import game.map.MapGenerator;
import game.map.presets.HexPreset;
import game.map.presets.MapPreset;
import networking.ServerState;
import networking.gamePackets.clientPackets.PacketClientInfo;
import networking.gamePackets.clientPackets.PacketClientKicked;
import networking.gamePackets.gamePackets.PacketRoundFinished;
import networking.gamePackets.gamePackets.PacketUnitMoved;
import networking.gamePackets.preGamePackets.*;
import networking.packets.Packet;
import networking.server.Server;
import networking.server.ServerListener;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ServerMain implements ServerListener {

	private Server server;
	private ServerState serverState;

	private Map<Socket, String> players;
	private Map<String, Boolean> playerReady;
	private Map<String, PlayerColor> playerColor;

	private Game game;

	private MapPreset preset = new HexPreset(51);

	public ServerMain() {
		serverState = ServerState.WAITING_FOR_PLAYERS;

		players = new HashMap<>();
		playerReady = new HashMap<>();
		playerColor = new HashMap<>();

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

	public ServerMain(int port) {
		serverState = ServerState.WAITING_FOR_PLAYERS;

		players = new HashMap<>();
		playerReady = new HashMap<>();
		playerColor = new HashMap<>();

		server = new Server(port, this);
	}

	public void playerQuit(String player) {
		Socket s = getPlayerSocket(player);

		System.out.println("PlayerQuit: " + player);

		players.keySet().stream()
				.filter(s2 -> s2 != s)
				.forEach(s2 -> server.sendPacket(s2, new PacketPlayerQuit(player)));

		players.remove(s);
		if (playerReady.containsKey(s)) playerReady.remove(s);
	}

	public void playerJoined(String player) {
		Socket s = getPlayerSocket(player);

		System.out.println("PlayerJoined: " + player);

		playerReady.put(player, false);
		playerColor.put(player, PlayerColor.BLUE);

		players.keySet().stream()
				.filter(s2 -> s2 != s)
				.forEach(s2 -> server.sendPacket(s, new PacketPlayerJoined(players.get(s2), playerReady.get(players.get(s2)), playerColor.get(players.get(s2)))));

		players.keySet().stream()
				.forEach(s2 -> server.sendPacket(s2, new PacketPlayerJoined(player, playerReady.get(player), playerColor.get(player))));
	}

	public void startGame() {

		System.out.println("All players ready - Generating map");
		PacketAllPlayersReady packet1 = new PacketAllPlayersReady();
		players.keySet().stream()
				.forEach(s -> server.sendPacket(s, packet1));

		for (String s: players.values()) {
			boolean f = false;
			do {
				f = false;

				for (String s2: players.values()) {
					if (s2.equals(s)) break;

					if (playerColor.get(s2) == playerColor.get(s)) {
						f = true;
					}
				}

				if (f) {
					playerColor.put(s, PlayerColor.values()[(int) (Math.random()*PlayerColor.values().length)]);
				}
			} while (f);
		}

		game = new Game(new MapGenerator(preset), playerColor);
		game.nextRound();

		System.out.println("StartGame");

		players.keySet().stream().forEach(s -> server.sendPacket(s, new PacketGameBegin(game)));
	}

	public void onReceivePacket(Socket s, Packet p) {
		if (p instanceof PacketUnitMoved) {
			PacketUnitMoved packet = (PacketUnitMoved) p;

			if (serverState == ServerState.INGAME && players.containsKey(s) && packet.getPlayer().equals(players.get(s)) && game.getPlayerTurn().equals(packet.getPlayer()) && packet.getUnit().getPlayer() == playerColor.get(players.get(s))) {
				Optional<Unit> u = game.getMap().getUnitAt(packet.getUnit().getX(), packet.getUnit().getY());
				if (u.isPresent()) {
					Unit unit = u.get();
					System.out.printf("%s moved Unit from %d / %d to %d / %d\n", packet.getPlayer(), unit.getX(), unit.getY(), packet.getTargetX(), packet.getTargetY());
					unit.moveTo(packet.getTargetX(), packet.getTargetY());

					players.keySet().stream()
							.forEach(s2 -> server.sendPacket(s2, packet));
				}
			} else {
				server.sendPacket(s, new PacketClientKicked("Hacker!"));
			}
		}

		if (p instanceof PacketRoundFinished) {
			PacketRoundFinished packet = (PacketRoundFinished) p;

			if (serverState == ServerState.INGAME && players.containsKey(s) && packet.getPlayer().equals(players.get(s)) && game.getPlayerTurn().equals(packet.getPlayer())) {
				System.out.printf("%s finished his round\n", packet.getPlayer());
				game.nextPlayer();
				players.keySet().stream()
						.forEach(s2 -> server.sendPacket(s2, packet));
			} else {
				server.sendPacket(s, new PacketClientKicked("Hacker!"));
			}
		}

		if (p instanceof PacketPlayerReady) {
			PacketPlayerReady packet = (PacketPlayerReady) p;
			if (serverState != ServerState.WAITING_FOR_PLAYERS || !players.containsKey(s)) return;

			if (players.get(s).equals(packet.getPlayer())) {
				System.out.println("PlayerReady: " + packet.getPlayer() + " " + packet.isReady());
				playerReady.put(packet.getPlayer(), packet.isReady());

				players.keySet().stream()
						.forEach(s2 -> server.sendPacket(s2, new PacketPlayerReady(packet.getPlayer(), packet.isReady())));

				if (!playerReady.values().contains(false) && players.keySet().size() > 1) {
					serverState = ServerState.INGAME;
					new Thread(() -> startGame()).start();
				}
			}
		}

		if (p instanceof PacketPlayerPickColor) {
			PacketPlayerPickColor packet = (PacketPlayerPickColor) p;
			if (serverState != ServerState.WAITING_FOR_PLAYERS || !players.containsKey(s)) return;

			if (players.get(s).equals(packet.getPlayer())) {

				System.out.println("PlayerPickColor: " + packet.getPlayer() + " " + packet.getColor().getDisplayName());
				playerColor.put(packet.getPlayer(), packet.getColor());

				players.keySet().stream()
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

			if (players.size() >= Math.min(PlayerColor.values().length, preset.getSpawnPoints().size())) {
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

	public void stop() {
		for(Socket s: players.keySet()) server.kickClient(s);
		server.close();
	}

	public void onClientJoin(Socket s) {
		System.out.println("Connected: " + s.getInetAddress());
	}

	public static void main(String[] args) {
		new ServerMain();
	}
}
