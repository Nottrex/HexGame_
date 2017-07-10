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

public class PacketUnitAttack implements Packet {
	private String player;
	private Unit unit;
	private Unit target;

	public PacketUnitAttack(String player, Unit unit, Unit target) {
		this.player = player;
		this.target = target;
		this.unit = unit;
	}

	public PacketUnitAttack(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);

		player = pd.readString();
		unit = PacketDecrypterUtil.getUnit(pd);
		target = PacketDecrypterUtil.getUnit(pd);
	}

	@Override
	public byte[] getData() {
		PacketBuilder pb = new PacketBuilder();

		pb.addString(player);
		PacketBuilderUtil.addUnit(pb, unit);
		PacketBuilderUtil.addUnit(pb, target);

		return pb.build();
	}

	public String getPlayer() {
		return player;
	}

	public Unit getUnit() {
		return unit;
	}
	public Unit getTarget() {
		return target;
	}
}
