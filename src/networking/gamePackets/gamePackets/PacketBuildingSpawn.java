package networking.gamePackets.gamePackets;

import game.Building;
import game.Unit;
import networking.gamePackets.util.PacketBuilderUtil;
import networking.gamePackets.util.PacketDecrypterUtil;
import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketBuildingSpawn implements Packet {
	private Building building;

	public PacketBuildingSpawn(Building building) {
		this.building = building;
	}

	public PacketBuildingSpawn(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);

		building = PacketDecrypterUtil.getBuilding(pd);
	}

	@Override
	public byte[] getData() {
		PacketBuilder pb = new PacketBuilder();

		PacketBuilderUtil.addBuilding(pb, building);

		return pb.build();
	}

	public Building getBuilding() {
		return building;
	}
}
