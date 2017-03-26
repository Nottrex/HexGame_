package networking.gamePackets.preGamePackets;

import game.enums.PlayerColor;
import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketPlayerPickColor implements Packet {
	private String player;
	private PlayerColor color;

	public PacketPlayerPickColor(String player, PlayerColor color) {
		this.player = player;
		this.color = color;
	}

	public PacketPlayerPickColor(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);

		player = pd.readString();
		color = PlayerColor.values()[pd.readInt()];
	}

	@Override
	public byte[] getData() {
		return new PacketBuilder().addString(player).addInt(color.ordinal()).build();
	}

	public String getPlayer() {
		return player;
	}

	public PlayerColor getColor() {
		return color;
	}
}
