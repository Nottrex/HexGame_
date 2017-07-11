package networking.server;

import networking.packets.Packet;

import java.net.Socket;

public interface ServerListener {
	void onReceivePacket(Socket s, Packet packet);

	void onClientLeave(Socket s);

	void onClientJoin(Socket s);
}
