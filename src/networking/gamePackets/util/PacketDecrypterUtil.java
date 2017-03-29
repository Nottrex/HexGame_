package networking.gamePackets.util;

import game.Unit;
import game.enums.PlayerColor;
import game.enums.UnitState;
import game.enums.UnitType;
import networking.packets.PacketDecrypter;

public class PacketDecrypterUtil {

	public static Unit getUnit(PacketDecrypter pd) {
		PlayerColor player = PlayerColor.values()[pd.readByte()];
		UnitState state = UnitState.values()[pd.readByte()];
		UnitType type = UnitType.values()[pd.readByte()];
		int x = pd.readInt();
		int y = pd.readInt();

		return new Unit(player, type, state, x, y);
	}
}