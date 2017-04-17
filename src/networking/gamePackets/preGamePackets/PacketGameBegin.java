package networking.gamePackets.preGamePackets;

import game.Game;
import game.Unit;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitState;
import game.enums.UnitType;
import game.map.GameMap;
import networking.gamePackets.util.PacketBuilderUtil;
import networking.gamePackets.util.PacketDecrypterUtil;
import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

import java.util.*;

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
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				fieldArray[x][y] = Field.values()[pd.readByte()];
			}
		}

		int unitAmount = pd.readInt();
		List<Unit> units = new ArrayList<>();
		for (int i = 0; i < unitAmount; i++) {
			units.add(PacketDecrypterUtil.getUnit(pd));
		}

		int[][] divMap = new int[width][height];
		Random r = new Random();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				divMap[x][y] = r.nextInt(fieldArray[x][y].getDiversity());
			}
		}

		GameMap map = new GameMap(fieldArray, units, divMap);

		game = new Game(map, players, round, playerTurnID);
	}

	@Override
	public byte[] getData() {
		PacketBuilder pb = new PacketBuilder();

		pb.addInt(game.getRound());
		pb.addInt(game.getPlayerTurnID());

		Map<String, PlayerColor> players = game.getPlayers();

		pb.addInt(players.size());

		for (String s: players.keySet()) {
			pb.addString(s);
			pb.addByte((byte) players.get(s).ordinal());
		}

		GameMap map = game.getMap();

		pb.addInt(map.getWidth());
		pb.addInt(map.getHeight());
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				pb.addByte((byte) map.getFieldAt(x, y).ordinal());
			}
		}

		pb.addInt(map.getUnits().size());
		for (Unit u: map.getUnits()) {
			PacketBuilderUtil.addUnit(pb, u);
		}

		return pb.build();
	}

	public Game getGame() {
		return game;
	}
}
