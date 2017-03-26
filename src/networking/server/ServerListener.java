package networking.server;

import networking.packets.Packet;

import java.net.Socket;

public interface ServerListener {
	public void onReceivePacket(Socket s, int packetID, Packet packet);
	
	public void onClientLeave(Socket s);
	
	public void onClientJoin(Socket s);
}
