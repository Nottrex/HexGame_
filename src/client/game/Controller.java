package client.game;

import client.window.KeyBindings;
import client.window.animationActions.*;
import game.Game;
import game.Location;
import game.Unit;
import game.enums.Direction;
import game.map.GameMap;
import game.util.ActionUtil;
import game.util.PossibleActions;
import networking.client.Client;
import networking.client.ClientListener;
import networking.gamePackets.clientPackets.PacketClientInfo;
import networking.gamePackets.gamePackets.PacketRoundFinished;
import networking.gamePackets.gamePackets.PacketUnitAttack;
import networking.gamePackets.gamePackets.PacketUnitMoved;
import networking.gamePackets.gamePackets.PacketUnitSpawn;
import networking.gamePackets.preGamePackets.PacketGameBegin;
import networking.packets.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller implements ClientListener {
	public Game game;
	private Camera camera;
	public Location selectedField = null;
	public Location hoverField = null;
	public PossibleActions pa = null;
	private Client client;
	private String userName;
	private ClientListener viewPacketListener;

	private int waitForPacket = 0;
	private List<AnimationAction> animationActions = new ArrayList<>();
	private long animationActionStart;

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 * Disconnects from server
	 */
	public void stopConnection() {
		game = null;
		if (client != null) client.close();
		client = null;
		viewPacketListener = null;
	}

	/**
	 * Connects to server
	 *
	 * @param userName you want to use
	 * @param hostName IP from the server
	 * @param port     that is forwarded and used in the server
	 */
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

	/**
	 * @return first animation to play
	 */
	public AnimationAction getAnimationAction() {
		if (animationActions.isEmpty()) return null;
		return animationActions.get(0);
	}

	/**
	 * Updates current {@link AnimationAction animation}
	 */
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

	/**
	 * Ends current {@link AnimationAction animation} and
	 * put the next one on its position
	 */
	private void animationActionFinished() {
		animationActions.get(0).finish();
		animationActions.remove(0);
		if (!animationActions.isEmpty()) animationActionStart = System.currentTimeMillis();
	}

	/**
	 * Adds a new {@link AnimationAction animation} to the queue
	 *
	 * @param aa animation to add
	 */
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

	/**
	 * Called when user clicks on a position
	 *
	 * @param l Clicked position
	 */
	public void onMouseClick(Location l) {
		if (waitForPacket > 0 || game == null) return;

		if (!animationActions.isEmpty()) {
			animationActionFinished();
			return;
		}

		GameMap m = game.getMap();
		if (selectedField == null) {
			if (game.getMap().getFieldAt(l).isAccessible())
				selectedField = l;
		} else {
			if (l.equals(selectedField)) {
				selectedField = null;
				return;
			}

			if (game.getMap().getFieldAt(l).isAccessible()) {
				Optional<Unit> u = m.getUnitAt(selectedField);
				Optional<Unit> u2 = m.getUnitAt(l);

				if (u.isPresent() && userName.equals(game.getPlayerTurn()) && u.get().getPlayer() == game.getPlayers().get(userName)) {
					Unit unit = u.get();

					pa = ActionUtil.getPossibleActions(game, unit);

					if (u2.isPresent() && pa.canAttack().contains(l)) {
						List<Direction> movement = pa.moveToToAttack(l);

						Location a = selectedField;
						for (Direction d : movement) {
							a = d.applyMovement(a);
						}

						sendPacket(new PacketUnitAttack(u.get(), u2.get(), a.x, a.y, movement));
						waitForPacket++;
						selectedField = null;
					} else if (pa.canMoveTo().contains(l)) {
						client.sendPacket(new PacketUnitMoved(unit, l.x, l.y, pa.moveTo(l)));
						waitForPacket++;
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

	/**
	 * Called when user types a key
	 *
	 * @param keyCode of the pressed keys
	 */
	public void onKeyType(int keyCode) {
		if (keyCode == KeyBindings.KEY_NEXT_PLAYER) {
			while (waitForPacket > 0) try {
				Thread.sleep(1);
			} catch (Exception e) {
			}
			if (game.getPlayerTurn().equals(userName)) {
				while (!animationActions.isEmpty())
					animationActionFinished();
				client.sendPacket(new PacketRoundFinished());
				waitForPacket++;
			}
		}
	}

	public void spawnUnit(Unit unit) {
		this.sendPacket(new PacketUnitSpawn(unit));
		waitForPacket++;
	}

	@Override
	public void onReceivePacket(Packet p) {

		if (p instanceof PacketUnitAttack) {
			PacketUnitAttack packet = (PacketUnitAttack) p;
			if (!packet.getDirections().isEmpty())
				addAnimationAction(new AnimationActionUnitMove(game, camera, packet.getUnit(), packet.getTargetX(), packet.getTargetY(), packet.getDirections()));
			addAnimationAction(new AnimationActionUnitAttack(game, camera, game.getMap().getGameUnit(packet.getUnit()), game.getMap().getGameUnit(packet.getTarget())));
			waitForPacket--;
		}


		if (p instanceof PacketUnitSpawn) {
			PacketUnitSpawn packet = (PacketUnitSpawn) p;
			addAnimationAction(new AnimationActionUnitSpawn(game, camera, packet.getUnit()));
			waitForPacket--;
		}

		if (p instanceof PacketRoundFinished) {
			PacketRoundFinished packet = (PacketRoundFinished) p;
			addAnimationAction(new AnimationActionRoundFinish(game, camera));
			waitForPacket--;
		}

		if (p instanceof PacketUnitMoved) {
			PacketUnitMoved packet = (PacketUnitMoved) p;
			addAnimationAction(new AnimationActionUnitMove(game, camera, packet.getUnit(), packet.getTargetX(), packet.getTargetY(), packet.getDirections()));
			waitForPacket--;
		}

		if (p instanceof PacketGameBegin) {
			PacketGameBegin packet = (PacketGameBegin) p;
			game = packet.getGame();
		}

		if (viewPacketListener != null) viewPacketListener.onReceivePacket(p);
	}

	public boolean playersTurn() {
		return userName.equals(game.getPlayerTurn());
	}

	@Override
	public void onLeave() {
		if (viewPacketListener != null) viewPacketListener.onLeave();
	}
}
