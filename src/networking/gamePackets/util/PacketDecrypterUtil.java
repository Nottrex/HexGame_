package networking.gamePackets.util;

import game.Building;
import game.Unit;
import game.enums.BuildingType;
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
		int stackSize = pd.readInt();
		float health = pd.readFloat();
		return new Unit(player, type, health, stackSize, state, x, y);
	}

	public static Building getBuilding(PacketDecrypter pd) {
		PlayerColor player = PlayerColor.values()[pd.readByte()];
		BuildingType type = BuildingType.values()[pd.readByte()];
		int x = pd.readInt();
		int y = pd.readInt();

		return new Building(x, y, type, player);
	}
}
