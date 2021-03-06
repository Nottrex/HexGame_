package networking.gamePackets.preGamePackets;

import game.Building;
import game.Game;
import game.Location;
import game.Unit;
import game.enums.Field;
import game.enums.PlayerColor;
import game.map.GameMap;
import networking.gamePackets.util.PacketBuilderUtil;
import networking.gamePackets.util.PacketDecrypterUtil;
import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketGameBegin implements Packet {
	private Game game;

	public PacketGameBegin(Game game) {
		this.game = game;
	}

	public PacketGameBegin(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);

		int round = pd.readInt();
		int playerTurnID = pd.readInt();

		int playerAmount = pd.readInt();
		HashMap<String, PlayerColor> players = new HashMap<>();
		for (int i = 0; i < playerAmount; i++) {
			String name = pd.readString();
			PlayerColor color = PlayerColor.values()[pd.readByte()];
			players.put(name, color);
		}

		int width = pd.readInt();
		int height = pd.readInt();
		Field[][] fieldArray = new Field[width][height];
		int[][] divMap = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				fieldArray[x][y] = Field.values()[pd.readByte()];
				divMap[x][y] = pd.readByte();
			}
		}

		int spawnPointAmount = pd.readInt();
		List<Location> spawnPoints = new ArrayList<>();
		for (int i = 0; i < spawnPointAmount; i++) {
			spawnPoints.add(new Location(pd.readInt(), pd.readInt()));
		}

		int unitAmount = pd.readInt();
		List<Unit> units = new ArrayList<>();
		for (int i = 0; i < unitAmount; i++) {
			units.add(PacketDecrypterUtil.getUnit(pd));
		}

		int buildingAmount = pd.readInt();
		List<Building> buildings = new ArrayList<>();
		for (int i = 0; i < buildingAmount; i++) {
			buildings.add(PacketDecrypterUtil.getBuilding(pd));
		}

		GameMap map = new GameMap(fieldArray, units, buildings, divMap, spawnPoints);

		game = new Game(map, players, round, playerTurnID);
	}

	@Override
	public byte[] getData() {
		PacketBuilder pb = new PacketBuilder();

		pb.addInt(game.getRound());
		pb.addInt(game.getPlayerTurnID());

		Map<String, PlayerColor> players = game.getPlayers();

		pb.addInt(players.size());

		for (String s : players.keySet()) {
			pb.addString(s);
			pb.addByte((byte) players.get(s).ordinal());
		}

		GameMap map = game.getMap();

		pb.addInt(map.getWidth());
		pb.addInt(map.getHeight());
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				pb.addByte((byte) map.getFieldAt(x, y).ordinal());
				pb.addByte((byte) map.getDiversityAt(x, y));
			}
		}

		pb.addInt(map.getMaxPlayers());
		for (Location l : map.getSpawnPoints()) {
			pb.addInt(l.x);
			pb.addInt(l.y);
		}

		pb.addInt(map.getUnits().size());
		for (Unit u : map.getUnits()) {
			PacketBuilderUtil.addUnit(pb, u);
		}

		pb.addInt(map.getBuildings().size());
		for (Building b: map.getBuildings()) {
			PacketBuilderUtil.addBuilding(pb, b);
		}

		return pb.build();
	}

	public Game getGame() {
		return game;
	}
}
