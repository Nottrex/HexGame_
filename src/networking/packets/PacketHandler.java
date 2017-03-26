package networking.packets;

public class PacketHandler {
	
	@SuppressWarnings("unchecked")
	private static Class<? extends Packet>[] packets = new Class[256];
	
	public static void setPacket(Class<? extends Packet> packet, int packetID) {
		packets[packetID] = packet;
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
