package networking.gamePackets.preGamePackets;

import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketPlayerQuit implements Packet {
	private String name;

	public PacketPlayerQuit(String name) {
		this.name = name;
	}

	public PacketPlayerQuit(byte[] data) {
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
