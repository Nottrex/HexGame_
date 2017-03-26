package networking.client;

import networking.packets.Packet;

public interface ClientListener {
	public void onReceivePacket(int packetID, Packet packet);

	public void onLeave();
}
