package networking.gamePackets.clientPackets;

import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketClientInfo implements Packet {
	private String clientName;
	private String clientVersion;

	public PacketClientInfo(String clientName, String clientVersion) {
		this.clientName = clientName;
		this.clientVersion = clientVersion;
	}

	public PacketClientInfo(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);
		clientVersion = pd.readString();
		clientName = pd.readString();
	}

	public byte[] getData() {
		return new PacketBuilder().addString(clientVersion).addString(clientName).build();
	}

	public String getClientName() {
		return clientName;
	}

	public String getClientVersion() {
		return clientVersion;
	}
}
