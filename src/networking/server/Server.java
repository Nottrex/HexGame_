package networking.server;

import networking.packets.Packet;
import networking.packets.PacketHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Server implements Runnable {
	private ServerSocket server;
	private List<Socket> clients;
	
	private ServerListener listener;
	
	public Server(int port, ServerListener listener) {		
		this.listener = listener;
		try {
			server = new ServerSocket(port);
			clients = new ArrayList<Socket>();
			
			new Thread(this).start();
		} catch (IOException e) {
			throw new RuntimeException("Error while creating Server Socket: " + e.getMessage());
		}
		
		System.out.println("Server started on port " + port + " on: ");
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements()) {
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration<InetAddress> ee = n.getInetAddresses();
			    while (ee.hasMoreElements()) {
			        InetAddress i = (InetAddress) ee.nextElement();
			        if (i.getAddress().length == 4) System.out.println(i.getHostAddress());
			    }
			}
		} catch (SocketException e1) {}
		
		System.out.println();
	}

	public void setServerListener(ServerListener listener) {
		this.listener = listener;
	}
	
	public void sendPacket(Socket s, Packet packet) {
		try {
			OutputStream os = s.getOutputStream();
			
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
	
	public void kickClient(Socket s) {
		try {
			s.close();
		} catch (IOException e) {}
	}
	
	public boolean isClosed() {
		return (server == null || server.isClosed());
	}
	
	public void close() {
		try {
			server.close();
		} catch (IOException e) {}
	}
	
	public ServerSocket getServerSocket() {
		return server;
	}
	
	public List<Socket> getClients() {
		return clients;
	}
	
	//PRIVATE
	
	private void onReceivePacket(Socket s, int packetID, Packet packet) {
		if (listener != null) listener.onReceivePacket(s, packetID, packet);
	}
	
	private void onClientLeave(Socket s) {
		if (clients.contains(s)) clients.remove(s);
		
		if (listener != null) listener.onClientLeave(s);
	}
	
	private void onClientJoin(final Socket s) {
		clients.add(s);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					InputStream is = s.getInputStream();
					while (!s.isClosed()) {
						int amount = is.read() << 24 | is.read() << 16 | is.read() << 8 | is.read();
						int packetID = is.read();
						byte[] data = new byte[amount];
						int i = is.read(data);
						
						for (; i < amount; i++) {
							data[i] = (byte) (is.read()-128);
						}
						try {
							onReceivePacket(s, packetID, PacketHandler.getPacket(packetID).getConstructor(byte[].class).newInstance(data));
						} catch (InstantiationException	| IllegalAccessException | IllegalArgumentException	| InvocationTargetException	| NoSuchMethodException | SecurityException e) {
							System.err.println("Error parsing packet: " + packetID);
						}				
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				onClientLeave(s);
			}
		}).start();
		
		if (listener != null) listener.onClientJoin(s);
	}
	
	@Override
	public void run() {
		while (!server.isClosed()) {
			try {
				Socket s = server.accept();
				onClientJoin(s);
			} catch (IOException e) {}
		}
	}
}
