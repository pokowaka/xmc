package net.pokowaka.xmc.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {
    private ByteBuffer byteBuffer;


    public ByteBufferInputStream(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        this.byteBuffer.position(0);
    }

    @Override
    public int read() throws IOException {
        if (byteBuffer.hasRemaining()) {
            int next = byteBuffer.get();
            return next == 0 ? -1 : next;
        } else {
            return -1;
        }
    }
}
