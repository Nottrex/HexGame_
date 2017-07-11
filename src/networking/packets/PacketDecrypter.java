package networking.packets;

import java.nio.ByteBuffer;

public class PacketDecrypter {

	private ByteBuffer buffer;

	public PacketDecrypter(byte[] data) {
		buffer = ByteBuffer.wrap(data);
	}

	public int readInt() {
		return buffer.getInt();
	}

	public long readLong() {
		return buffer.getLong();
	}

	public double readDouble() {
		return buffer.getDouble();
	}

	public float readFloat() {
		return buffer.getFloat();
	}

	public short readShort() {
		return buffer.getShort();
	}

	public char readChar() {
		return buffer.getChar();
	}

	public byte readByte() {
		return buffer.get();
	}

	public boolean readBoolean() {
		return (buffer.get() & 1) == 1;
	}

	public String readString() {
		int lenght = buffer.getInt();
		char[] chars = new char[lenght];
		for (int i = 0; i < lenght; i++) {
			chars[i] = buffer.getChar();
		}

		return new String(chars);
	}
}
