package networking.gamePackets.gamePackets;

import game.Unit;
import game.enums.Direction;
import networking.gamePackets.util.PacketBuilderUtil;
import networking.gamePackets.util.PacketDecrypterUtil;
import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

import java.util.ArrayList;
import java.util.List;

public class PacketUnitSpawn implements Packet {
	private String player;
	private Unit unit;

	public PacketUnitSpawn(String player, Unit unit) {
		this.player = player;
		this.unit = unit;
	}

	public PacketUnitSpawn(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);

		player = pd.readString();

		unit = PacketDecrypterUtil.getUnit(pd);
	}

	@Override
	public byte[] getData() {
		PacketBuilder pb = new PacketBuilder();

		pb.addString(player);

		PacketBuilderUtil.addUnit(pb, unit);

		return pb.build();
	}

	public String getPlayer() {
		return player;
	}

	public Unit getUnit() {
		return unit;
	}
}
