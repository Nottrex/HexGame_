package server;

import game.Game;
import networking.gamePackets.gamePackets.PacketRoundFinished;
import networking.gamePackets.gamePackets.PacketUnitMoved;
import networking.gamePackets.gamePackets.PacketUnitSpawn;
import networking.packets.Packet;

public class Anticheat {

	public static boolean legalMove(Packet p, Game game, String player) {
		if (p instanceof PacketRoundFinished) {
			if (!game.getPlayerTurn().equals(player)) return false;
		}

		if (p instanceof PacketUnitSpawn) {
			PacketUnitSpawn packet = (PacketUnitSpawn) p;
			if (game.getMap().getUnitAt(packet.getUnit().getX(), packet.getUnit().getY()).isPresent()) return false;
			if (!game.getMap().getFieldAt(packet.getUnit().getX(), packet.getUnit().getY()).isAccessible())
				return false;
			if (game.getPlayerMoney(game.getPlayers().get(player)) < packet.getUnit().getType().getCost()) return false;
			if (!player.equals(game.getPlayerTurn())) return false;
		}

		if (p instanceof PacketUnitMoved) {
			PacketUnitMoved packet = (PacketUnitMoved) p;

		}

		return true;
	}
}
