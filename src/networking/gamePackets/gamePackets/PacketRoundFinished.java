package networking.gamePackets.gamePackets;

import networking.packets.Packet;
import networking.packets.PacketBuilder;

public class PacketRoundFinished implements Packet {

	public PacketRoundFinished() {
	}

	public PacketRoundFinished(byte[] data) {

	}

	@Override
	public byte[] getData() {
		return new PacketBuilder().build();
	}
}
