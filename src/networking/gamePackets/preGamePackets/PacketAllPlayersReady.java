package networking.gamePackets.preGamePackets;

import networking.packets.Packet;

public class PacketAllPlayersReady implements Packet {

	public PacketAllPlayersReady() {

	}

	public PacketAllPlayersReady(byte[] data) {

	}

	@Override
	public byte[] getData() {
		return new byte[1];
	}
}
