package networking.client;

import networking.packets.Packet;

public interface ClientListener {
	void onReceivePacket(Packet packet);

	void onLeave();
}
