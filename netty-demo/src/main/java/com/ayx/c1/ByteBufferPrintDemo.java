package com.ayx.c1;

import com.ayx.util.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * ByteBuffer工具类 演示内部结构
 */
public class ByteBufferPrintDemo {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put(new byte[]{0x64});
        ByteBufferUtil.debugAll(byteBuffer);

        byteBuffer.put(new byte[]{0x65,0x66});
        ByteBufferUtil.debugAll(byteBuffer);

        byteBuffer.flip();
        System.out.println((char) byteBuffer.get());
        ByteBufferUtil.debugAll(byteBuffer);

        byteBuffer.clear();
        byteBuffer.put(new byte[]{0x67});
        ByteBufferUtil.debugAll(byteBuffer);


    }
}
