package networking.gamePackets.clientPackets;

import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketClientKicked implements Packet {
	private String reason;

	public PacketClientKicked(String reason) {
		this.reason = reason;
	}

	public PacketClientKicked(byte[] data) {
		reason = new PacketDecrypter(data).readString();
	}

	@Override
	public byte[] getData() {
		return new PacketBuilder().addString(reason).build();
	}

	public String getReason() {
		return reason;
	}
}
