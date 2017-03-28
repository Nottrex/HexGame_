package networking.client;

import networking.packets.Packet;
import networking.packets.PacketHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class Client implements Runnable {
	private Socket socket;	
	private OutputStream os;
	private InputStream is;
	
	private ClientListener listener;
	
	public Client(String hostName, int port, ClientListener listener) {
		this.listener = listener;
		
		try {
			socket = new Socket(hostName, port);
			os = socket.getOutputStream();
			is = socket.getInputStream();
			
			new Thread(this).start();
		} catch (IOException e) {
			if (socket != null && !socket.isClosed())
				try {socket.close();} catch (IOException e1) {}
			throw new RuntimeException("Error while creating Socket: " + e.getMessage());
		}
	}
	
	public void setClientListener(ClientListener listener) {
		this.listener = listener;
	}
	
	public boolean isClosed() {
		return (socket == null || socket.isClosed());
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
	
	public void sendPacket(Packet packet) {
		try {
			byte[] data = packet.getData();
			
			os.write(data.length >> 24);
			os.write(data.length >> 16);
			os.write(data.length >> 8);
			os.write(data.length);
			os.write(PacketHandler.getPacketID(packet.getClass()));
			os.write(data);
			os.flush();			
		} catch (IOException e) {}
	}
	
	//Private
	
	private void onReceivePacket(Packet packet) {
		listener.onReceivePacket(packet);
	}

	private void onLeave() {
		listener.onLeave();
	}
	
	@Override
	public void run() {
		try {
			while (!socket.isClosed()) {
				int amount = is.read() << 24 | is.read() << 16 | is.read() << 8 | is.read();

				int packetID = is.read();
				byte[] data = new byte[amount];
				int i = is.read(data);
				
				for (; i < amount; i++) {
					data[i] = (byte) (is.read()-128);
				}
				try {
					onReceivePacket(PacketHandler.getPacket(packetID).getConstructor(byte[].class).newInstance(data));
				} catch (InstantiationException	| IllegalAccessException | IllegalArgumentException	| InvocationTargetException	| NoSuchMethodException | SecurityException e) {
					System.err.println("Error parsing packet: " + packetID);
					e.printStackTrace();
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		onLeave();
	}
}
