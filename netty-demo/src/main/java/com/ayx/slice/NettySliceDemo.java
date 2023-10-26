package com.ayx.slice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class NettySliceDemo {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(new byte[]{1,2,3,4,5,6,7,8,9,10});

    }
}
