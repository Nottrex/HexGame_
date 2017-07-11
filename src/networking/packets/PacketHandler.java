package networking.packets;

import networking.gamePackets.clientPackets.PacketClientInfo;
import networking.gamePackets.clientPackets.PacketClientKicked;
import networking.gamePackets.gamePackets.PacketRoundFinished;
import networking.gamePackets.gamePackets.PacketUnitAttack;
import networking.gamePackets.gamePackets.PacketUnitMoved;
import networking.gamePackets.gamePackets.PacketUnitSpawn;
import networking.gamePackets.preGamePackets.*;

public class PacketHandler {

	private static Class<? extends Packet>[] packets = new Class[256];
	@SuppressWarnings("unchecked")

	private static int id = 0;

	static {
		addPacket(PacketClientInfo.class);
		addPacket(PacketClientKicked.class);

		addPacket(PacketPlayerReady.class);
		addPacket(PacketPlayerPickColor.class);

		addPacket(PacketAllPlayersReady.class);
		addPacket(PacketGameBegin.class);
		addPacket(PacketPlayerQuit.class);
		addPacket(PacketPlayerJoined.class);

		addPacket(PacketUnitMoved.class);
		addPacket(PacketRoundFinished.class);
		addPacket(PacketUnitSpawn.class);
		addPacket(PacketUnitAttack.class);
	}

	private PacketHandler() {
	}

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

}
