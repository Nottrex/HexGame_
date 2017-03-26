package networking.packets;

import networking.gamePackets.clientPackets.PacketClientInfo;
import networking.gamePackets.clientPackets.PacketClientKicked;
import networking.gamePackets.clientPackets.PacketServerInfo;
import networking.gamePackets.preGamePackets.*;

public class PacketHandler {

	static {
		addPacket(PacketServerInfo.class);
		addPacket(PacketClientInfo.class);
		addPacket(PacketClientKicked.class);

		addPacket(PacketGameBegin.class);
		addPacket(PacketPlayerQuit.class);
		addPacket(PacketPlayerJoined.class);

		addPacket(PacketPlayerReady.class);
		addPacket(PacketPlayerPickColor.class);
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Packet>[] packets = new Class[256];


	private static int id = 0;
	private static void addPacket(Class<? extends Packet> packet) {
		packets[id++] = packet;
	}
	
	public static Class<? extends Packet> getPacket(int packetID) {
		return packets[packetID];
	}

	public static int getPacketID(Class<? extends Packet> packet) {
		for (int i = 0; i < packets.length; i++) {
			if (packets[i] == packet) return i;
		}
		return -1;
	}

	private PacketHandler() {}
	
}
