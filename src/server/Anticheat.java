package server;

import game.Game;
import networking.packets.Packet;

import java.net.Socket;

public class Anticheat {

	public static boolean legalMove(Packet p, Game game, Socket socket) {
		return true;
	}
}
