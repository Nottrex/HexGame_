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
	private Unit unit, target;
	private int targetX, targetY;
	private List<Direction> directions;

	public PacketUnitAttack(Unit unit, Unit target, int targetX, int targetY, List<Direction> directions) {
		this.unit = unit;
		this.target = target;
		this.targetX = targetX;
		this.targetY = targetY;
		this.directions = directions;
	}

	public PacketUnitAttack(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);

		unit = PacketDecrypterUtil.getUnit(pd);

		target = PacketDecrypterUtil.getUnit(pd);

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
		PacketBuilderUtil.addUnit(pb, target);

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

	public Unit getTarget() {
		return target;
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
