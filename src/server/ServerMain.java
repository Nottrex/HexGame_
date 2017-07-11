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
import networking.gamePackets.gamePackets.PacketUnitAttack;
import networking.gamePackets.gamePackets.PacketUnitMoved;
import networking.gamePackets.gamePackets.PacketUnitSpawn;
import networking.gamePackets.preGamePackets.*;
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

	private Game game;

	private MapPreset preset = new HexPreset(201);

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

	public ServerMain(MapPreset preset, int port) {
		this.preset = preset;

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

	public void onReceiveGamePacket(Socket s, Packet p) {
		if (!Anticheat.legalMove(p, game, players.get(s))) {
			server.sendPacket(s, new PacketClientKicked("Hacker!"));
			return;
		}

		if (p instanceof PacketUnitAttack) {
			PacketUnitAttack packet = (PacketUnitAttack) p;
			Unit unit = game.getMap().getGameUnit(packet.getUnit());
			Unit target = game.getMap().getGameUnit(packet.getTarget());
			if (!packet.getDirections().isEmpty()) {
				System.out.printf("moved Unit from %d / %d to %d / %d\n", packet.getUnit().getX(), packet.getUnit().getY(), packet.getTargetX(), packet.getTargetY());
				unit.moveTo(packet.getTargetX(), packet.getTargetY());
			}

			System.out.printf("attacked Unit at %d / %d with Unit at %d / %d\n", packet.getTarget().getX(), packet.getTarget().getY(), packet.getUnit().getX(), packet.getUnit().getY());
			game.getMap().attack(unit, target);
			players.keySet().stream().forEach(s2 -> server.sendPacket(s2, packet));
		}

		if (p instanceof PacketUnitSpawn) {
			PacketUnitSpawn packet = (PacketUnitSpawn) p;

			System.out.printf("spawned Unit at %d / %d\n", packet.getUnit().getX(), packet.getUnit().getY());
			game.getMap().spawnUnit(packet.getUnit());
			players.keySet().stream().forEach(s2 -> server.sendPacket(s2, packet));
		}

		if (p instanceof PacketUnitMoved) {
			PacketUnitMoved packet = (PacketUnitMoved) p;

			Unit unit = game.getMap().getGameUnit(packet.getUnit());
			System.out.printf("moved Unit from %d / %d to %d / %d\n", unit.getX(), unit.getY(), packet.getTargetX(), packet.getTargetY());
			unit.moveTo(packet.getTargetX(), packet.getTargetY());

			players.keySet().stream()
					.forEach(s2 -> server.sendPacket(s2, packet));
		}

		if (p instanceof PacketRoundFinished) {
			PacketRoundFinished packet = (PacketRoundFinished) p;

			System.out.printf("finished his round\n");
			game.nextPlayer();
			players.keySet().stream()
					.forEach(s2 -> server.sendPacket(s2, packet));
		}
	}

	public void onReceivePacket(Socket s, Packet p) {
		if (serverState == ServerState.INGAME && players.containsKey(s)) onReceiveGamePacket(s, p);

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

				System.out.println("PlayerPickColor: " + packet.getPlayer() + " " + packet.getColor());
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
