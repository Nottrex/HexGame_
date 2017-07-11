package networking.gamePackets.gamePackets;

import game.Unit;
import networking.gamePackets.util.PacketBuilderUtil;
import networking.gamePackets.util.PacketDecrypterUtil;
import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketUnitSpawn implements Packet {
	private Unit unit;

	public PacketUnitSpawn(Unit unit) {
		this.unit = unit;
	}

	public PacketUnitSpawn(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);

		unit = PacketDecrypterUtil.getUnit(pd);
	}

	@Override
	public byte[] getData() {
		PacketBuilder pb = new PacketBuilder();

		PacketBuilderUtil.addUnit(pb, unit);

		return pb.build();
	}

	public Unit getUnit() {
		return unit;
	}
}
