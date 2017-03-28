package client;

import game.Game;
import game.map.GameMap;
import game.Location;
import game.Unit;
import game.enums.Direction;
import game.enums.PlayerColor;
import game.enums.UnitState;
import game.util.ActionUtil;
import game.util.PossibleActions;
import networking.client.Client;
import networking.client.ClientListener;
import networking.gamePackets.clientPackets.PacketClientInfo;
import networking.gamePackets.preGamePackets.PacketGameBegin;
import networking.packets.Packet;

import java.util.List;
import java.util.Optional;

public class Controller implements ClientListener {

	private Client client;
	private String userName;

	public Game game;
	public Location selectedField = null;
	public PossibleActions pa = null;

	private ClientListener viewPacketListener;

	public void stopConnection() {
		game = null;
		if (client != null) client.close();
		client = null;
		viewPacketListener = null;
	}

	public void connect(String userName, String hostName, int port) {
		if (client != null) client.close();

		this.userName = userName;
		try {
			client = new Client(hostName, port, this);
			client.sendPacket(new PacketClientInfo(userName, Game.VERSION));
		} catch (Exception e) {
			onLeave();
		}

	}

	public void sendPacket(Packet packet) {
		if (client != null) client.sendPacket(packet);
	}

	public void setViewPacketListener(ClientListener clientListener) {
		this.viewPacketListener = clientListener;
	}

	public void onMouseClick(Location l) {
		if (game == null) return;

		GameMap m = game.getMap();
		if (selectedField == null) {
			if (game.getMap().getFieldAt(l).isAccessible())
				selectedField = l;
		} else {
			if (game.getMap().getFieldAt(l).isAccessible()) {
				Optional<Unit> u = m.getUnitAt(selectedField);
				Optional<Unit> u2 = m.getUnitAt(l);

				if (u.isPresent() && userName.equals(game.getPlayerTurn())) {
					Unit unit = u.get();
					pa = ActionUtil.getPossibleActions(game, unit);

					if (u2.isPresent() && pa.canAttack().contains(l)) {
						List<Direction> movement = pa.moveToToAttack(l);

						Location a = selectedField;
						for (Direction d: movement) {
							a = d.applyMovement(a);
						}

						unit.setX(a.x);
						unit.setY(a.y);

						//TODO: ATTACK UNITS?

						unit.setState(UnitState.INACTIVE);
						selectedField = null;
					} else if (pa.canMoveTo().contains(l)) {
						unit.setX(l.x);
						unit.setY(l.y);

						unit.setState(UnitState.MOVED);
						selectedField = null;
					} else selectedField = l;
				} else selectedField = l;
			} else selectedField = null;
		}

		if (selectedField != null) {
			Optional<Unit> u = m.getUnitAt(selectedField);
			if (u.isPresent()) pa = ActionUtil.getPossibleActions(game, u.get());
		}
	}

	public void onKeyType(int keyCode) {
		if (keyCode == KeyBindings.KEY_NEXT_PLAYER) {
			game.nextPlayer();
			selectedField = null;
		}
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public void onReceivePacket(Packet p) {
		if (p instanceof PacketGameBegin) {
			PacketGameBegin packet = (PacketGameBegin) p;
			game = packet.getGame();
		}

		if (viewPacketListener != null) viewPacketListener.onReceivePacket(p);
	}

	@Override
	public void onLeave() {
		if (viewPacketListener != null) viewPacketListener.onLeave();
	}
}
