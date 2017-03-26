package networking.gamePackets.clientPackets;

import networking.ServerState;
import networking.packets.Packet;
import networking.packets.PacketBuilder;
import networking.packets.PacketDecrypter;

public class PacketServerInfo implements Packet {
	private String serverVersion;
	private ServerState serverState;

	public PacketServerInfo(String serverVersion, ServerState serverState) {
		this.serverVersion = serverVersion;
		this.serverState = serverState;
	}

	public PacketServerInfo(byte[] data) {
		PacketDecrypter pd = new PacketDecrypter(data);
		serverVersion = pd.readString();
		serverState = ServerState.values()[pd.readInt()];
	}

	public byte[] getData() {
		return new PacketBuilder().addString(serverVersion).addInt(serverState.ordinal()).build();
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public ServerState getServerState() {
		return serverState;
	}
}
