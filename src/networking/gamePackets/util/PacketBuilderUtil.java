package networking.gamePackets.util;

import game.Building;
import game.Unit;
import networking.packets.PacketBuilder;

public class PacketBuilderUtil {

	public static void addUnit(PacketBuilder pb, Unit unit) {
		pb.addByte((byte) unit.getPlayer().ordinal());
		pb.addByte((byte) unit.getState().ordinal());
		pb.addByte((byte) unit.getType().ordinal());
		pb.addInt(unit.getX());
		pb.addInt(unit.getY());
		pb.addInt(unit.getStackSize());
		pb.addFloat(unit.getHealth());
	}

	public static void addBuilding(PacketBuilder pb, Building building) {
		pb.addByte((byte) building.getPlayer().ordinal());
		pb.addByte((byte) building.getType().ordinal());
		pb.addInt(building.getX());
		pb.addInt(building.getY());
	}
}
