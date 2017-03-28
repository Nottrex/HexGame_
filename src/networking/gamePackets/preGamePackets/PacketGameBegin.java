package networking.gamePackets.preGamePackets;

import game.Game;
import game.Unit;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitState;
import game.enums.UnitType;
import game.map.GameMap;
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
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				fieldArray[x][y] = Field.values()[pd.readByte()];
			}
		}

		int unitAmount = pd.readInt();
		List<Unit> units = new ArrayList<>();
		for (int i = 0; i < unitAmount; i++) {
			int x = pd.readInt();
			int y = pd.readInt();
			UnitState state = UnitState.values()[pd.readByte()];
			PlayerColor player = PlayerColor.values()[pd.readByte()];
			UnitType type = UnitType.values()[pd.readByte()];
			units.add(new Unit(player, type, state, x, y));
		}

		GameMap map = new GameMap(fieldArray, units);

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
			pb.addInt(u.getX());
			pb.addInt(u.getY());
			pb.addByte((byte) u.getState().ordinal());
			pb.addByte((byte) u.getPlayer().ordinal());
			pb.addByte((byte) u.getType().ordinal());
		}

		return pb.build();
	}

	public Game getGame() {
		return game;
	}
}
