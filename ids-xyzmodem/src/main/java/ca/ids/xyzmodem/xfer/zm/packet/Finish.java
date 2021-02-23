package ca.ids.xyzmodem.xfer.zm.packet;


import ca.ids.xyzmodem.xfer.util.Buffer;
import ca.ids.xyzmodem.xfer.util.ByteBuffer;
import ca.ids.xyzmodem.xfer.zm.util.ZMPacket;

public class Finish extends ZMPacket {

	@Override
	public Buffer marshall() {
		ByteBuffer buff = ByteBuffer.allocateDirect(16);

		for(int i=0;i<2;i++)
			buff.put((byte) 'O');

		buff.flip();

		return buff;
	}

	@Override
	public String toString() {
		return "Finish: OO";
	}

}
