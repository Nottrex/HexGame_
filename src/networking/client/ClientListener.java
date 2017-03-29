package networking.client;

import networking.packets.Packet;

public interface ClientListener {
	public void onReceivePacket(Packet packet);

	public void onLeave();
}
