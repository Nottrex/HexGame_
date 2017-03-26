package networking.gamePackets.preGamePackets;

import game.enums.PlayerColor;
import game.map.GameMap;
import networking.packets.Packet;

import java.util.Map;

public class PacketGameBegin implements Packet {
	private GameMap map;

	private Map<String, PlayerColor> players;

	public PacketGameBegin(GameMap map, Map<String, PlayerColor> players) {
		this.map = map;
		this.players = players;
	}

	public PacketGameBegin(byte[] data) {
		//TODO: Put stuff into packet
	}

	@Override
	public byte[] getData() {
		return new byte[0];
	}

	public GameMap getMap() {
		return map;
	}

	public Map<String, PlayerColor> getPlayers() {
		return players;
	}
}
