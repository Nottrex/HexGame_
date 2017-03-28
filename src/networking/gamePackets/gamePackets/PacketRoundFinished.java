package networking.gamePackets.gamePackets;

import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketRoundFinished implements Packet {
	private String player;

	public PacketRoundFinished(String player) {
		this.player = player;
	}

	public PacketRoundFinished(byte[] data) {
		player = new PacketDecrypter(data).readString();
	}

	@Override
	public byte[] getData() {
		return new PacketBuilder().addString(player).build();
	}

	public String getPlayer() {
		return player;
	}
}
