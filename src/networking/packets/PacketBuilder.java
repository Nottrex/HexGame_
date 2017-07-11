package networking.packets;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class PacketBuilder {
	private int totalSize;
	private List<byte[]> data;

	public PacketBuilder() {
		data = new LinkedList<>();
		totalSize = 0;
	}

	public PacketBuilder addInt(int i) {
		totalSize += 4;
		data.add(ByteBuffer.allocate(4).putInt(i).array());
		return this;
	}

	public PacketBuilder addLong(long l) {
		totalSize += 8;
		data.add(ByteBuffer.allocate(8).putLong(l).array());
		return this;
	}

	public PacketBuilder addChar(char c) {
		totalSize += 2;
		data.add(ByteBuffer.allocate(2).putChar(c).array());
		return this;
	}

	public PacketBuilder addString(String s) {
		addInt(s.length());
		for (char c : s.toCharArray()) {
			addChar(c);
		}
		return this;
	}

	public PacketBuilder addShort(short s) {
		totalSize += 2;
		data.add(ByteBuffer.allocate(2).putShort(s).array());
		return this;
	}

	public PacketBuilder addByte(byte b) {
		totalSize += 1;
		data.add(new byte[]{b});
		return this;
	}

	public PacketBuilder addFloat(float f) {
		totalSize += 4;
		data.add(ByteBuffer.allocate(4).putFloat(f).array());
		return this;
	}

	public PacketBuilder addDouble(double d) {
		totalSize += 8;
		data.add(ByteBuffer.allocate(8).putDouble(d).array());
		return this;
	}

	public PacketBuilder addBoolean(boolean b) {
		addByte((byte) (b ? 1 : 0));
		return this;
	}

	public byte[] build() {
		byte[] result = new byte[totalSize];

		int i = 0;
		for (byte[] array : data) {
			for (byte b : array) {
				result[i] = b;
				i++;
			}
		}

		return result;
	}
}
