package networking.gamePackets.preGamePackets;

import game.enums.PlayerColor;
import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketPlayerJoined implements Packet {
	private String name;
	private boolean ready;
	private PlayerColor color;

	public PacketPlayerJoined(String name, boolean ready, PlayerColor color) {
		this.name = name;
		this.ready = ready;
		this.color = color;
	}

	public PacketPlayerJoined(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);
		name = pd.readString();
		ready = pd.readBoolean();
		color = PlayerColor.values()[pd.readInt()];
	}

	@Override
	public byte[] getData() {
		return new PacketBuilder().addString(name).addBoolean(ready).addInt(color.ordinal()).build();
	}

	public PlayerColor getColor() {
		return color;
	}

	public boolean isReady() {
		return ready;
	}

	public String getName() {
		return name;
	}
}
