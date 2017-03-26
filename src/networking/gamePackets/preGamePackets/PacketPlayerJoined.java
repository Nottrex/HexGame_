package networking.gamePackets.preGamePackets;

import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketPlayerJoined implements Packet {
	private String name;

	public PacketPlayerJoined(String name) {
		this.name = name;
	}

	public PacketPlayerJoined(byte[] data) {
		name = new PacketDecrypter(data).readString();
	}

	@Override
	public byte[] getData() {
		return new PacketBuilder().addString(name).build();
	}

	public String getName() {
		return name;
	}
}
