package com.ayx.bytebuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

@Slf4j
public class NettyByteBufferDemo {
    public static void main(String[] args) {
        //和NIO中的ByteBuffer不同的是 netty中的可以自动扩容
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        log.info(byteBuf.getClass().toString());
        log(byteBuf);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 500; i++) {
            sb.append("a");
        }
        byteBuf.writeBytes(sb.toString().getBytes());
        log(byteBuf);
    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
