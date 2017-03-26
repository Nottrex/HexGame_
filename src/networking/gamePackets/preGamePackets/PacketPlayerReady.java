package networking.gamePackets.preGamePackets;

import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketPlayerReady implements Packet {
	private boolean ready;
	private String player;

	public PacketPlayerReady(String player, boolean ready) {
		this.ready = ready;
		this.player = player;
	}

	public PacketPlayerReady(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);
		ready = pd.readBoolean();
		player = pd.readString();
	}

	@Override
	public byte[] getData() {
		return new PacketBuilder().addBoolean(ready).addString(player).build();
	}

	public boolean isReady() {
		return ready;
	}

	public String getPlayer() {
		return player;
	}
}
