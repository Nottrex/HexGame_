package client;

import client.animationActions.AnimationActionRoundFinish;
import client.animationActions.AnimationActionUnitMove;
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
import networking.gamePackets.gamePackets.PacketRoundFinished;
import networking.gamePackets.gamePackets.PacketUnitMoved;
import networking.gamePackets.preGamePackets.PacketGameBegin;
import networking.packets.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller implements ClientListener {

	private Client client;
	private String userName;

	public Game game;
	public Location selectedField = null;
	public PossibleActions pa = null;

	private ClientListener viewPacketListener;

	private boolean waitForPacket = false;
	private List<AnimationAction> animationActions = new ArrayList<>();
	private long animationActionStart;

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

	public AnimationAction getAnimationAction() {
		if (animationActions.isEmpty()) return null;
		return animationActions.get(0);
	}

	public void updateAnimationActions() {
		long time = System.currentTimeMillis();
		if (!animationActions.isEmpty()) {
			if (time - animationActionStart >= animationActions.get(0).getTotalTime()) {
				animationActionFinished();
			} else {
				animationActions.get(0).update(time - animationActionStart);
			}
		}

	}

	private void animationActionFinished() {
		animationActions.get(0).finish();
		animationActions.remove(0);
		if (!animationActions.isEmpty()) animationActionStart = System.currentTimeMillis();
	}

	private void addAnimationAction(AnimationAction aa) {
		selectedField = null;
		animationActions.add(aa);

		if (animationActions.size() == 1) {
			animationActionStart = System.currentTimeMillis();
		}
	}

	public void sendPacket(Packet packet) {
		if (client != null) client.sendPacket(packet);
	}

	public void setViewPacketListener(ClientListener clientListener) {
		this.viewPacketListener = clientListener;
	}

	public void onMouseClick(Location l) {
		if (waitForPacket || game == null) return;

		if (!animationActions.isEmpty()) {
			animationActionFinished();
			return;
		}

		GameMap m = game.getMap();
		if (selectedField == null) {
			if (game.getMap().getFieldAt(l).isAccessible())
				selectedField = l;
		} else {
			if (game.getMap().getFieldAt(l).isAccessible()) {
				Optional<Unit> u = m.getUnitAt(selectedField);
				Optional<Unit> u2 = m.getUnitAt(l);

				if (u.isPresent() && userName.equals(game.getPlayerTurn()) && u.get().getPlayer() == game.getPlayers().get(userName)) {
					Unit unit = u.get();

					pa = ActionUtil.getPossibleActions(game, unit);

					if (u2.isPresent() && pa.canAttack().contains(l)) {
						List<Direction> movement = pa.moveToToAttack(l);

						if (!movement.isEmpty()) {
							Location a = selectedField;
							for (Direction d: movement) {
								a = d.applyMovement(a);
							}

							client.sendPacket(new PacketUnitMoved(userName, unit, a.x, a.y, movement));

							waitForPacket = true;
							selectedField = null;
						}


						//TODO: ATTACK UNITS?
					} else if (pa.canMoveTo().contains(l)) {
						client.sendPacket(new PacketUnitMoved(userName, unit, l.x, l.y, pa.moveTo(l)));
						waitForPacket = true;
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
			if (game.getPlayerTurn().equals(userName)) {
				client.sendPacket(new PacketRoundFinished(userName));
				waitForPacket = true;
			}
		}
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public void onReceivePacket(Packet p) {
		if (p instanceof PacketRoundFinished) {
			PacketRoundFinished packet = (PacketRoundFinished) p;
			waitForPacket = false;
			addAnimationAction(new AnimationActionRoundFinish(game));
		}

		if (p instanceof PacketUnitMoved) {
			PacketUnitMoved packet = (PacketUnitMoved) p;
			waitForPacket = false;
			addAnimationAction(new AnimationActionUnitMove(game, game.getMap().getUnitAt(packet.getUnit().getX(), packet.getUnit().getY()).get(), packet.getTargetX(), packet.getTargetY(), packet.getDirections()));
		}

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
