package networking.gamePackets.util;

import game.Unit;
import networking.packets.PacketBuilder;

public class PacketBuilderUtil {

	public static void addUnit(PacketBuilder pb, Unit unit) {
		pb.addByte((byte) unit.getPlayer().ordinal());
		pb.addByte((byte) unit.getState().ordinal());
		pb.addByte((byte) unit.getType().ordinal());
		pb.addInt(unit.getX());
		pb.addInt(unit.getY());
	}


}
