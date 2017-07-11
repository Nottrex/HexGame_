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

public class PacketUnitMoved implements Packet {
	private Unit unit;
	private int targetX, targetY;
	private List<Direction> directions;

	public PacketUnitMoved(Unit unit, int targetX, int targetY, List<Direction> directions) {
		this.unit = unit;
		this.targetX = targetX;
		this.targetY = targetY;
		this.directions = directions;
	}

	public PacketUnitMoved(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);

		unit = PacketDecrypterUtil.getUnit(pd);

		targetX = pd.readInt();
		targetY = pd.readInt();

		int size = pd.readInt();
		directions = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			directions.add(Direction.values()[pd.readByte()]);
		}
	}

	@Override
	public byte[] getData() {
		PacketBuilder pb = new PacketBuilder();

		PacketBuilderUtil.addUnit(pb, unit);

		pb.addInt(targetX);
		pb.addInt(targetY);

		pb.addInt(directions.size());
		for (Direction d : directions) {
			pb.addByte((byte) d.ordinal());
		}

		return pb.build();
	}

	public Unit getUnit() {
		return unit;
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	public List<Direction> getDirections() {
		return directions;
	}
}
