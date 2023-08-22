package com.ayx.c1;

import com.ayx.util.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * ByteBuffer和String相互转换
 */
public class ByteBufferStringDemo {
    public static void main(String[] args) {
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(10);
        byteBuffer1.put("hello".getBytes(StandardCharsets.UTF_8));
        ByteBufferUtil.debugAll(byteBuffer1);

        ByteBuffer byteBuffer2 = ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8));
        ByteBufferUtil.debugAll(byteBuffer2);

        ByteBuffer byteBuffer3 = StandardCharsets.UTF_8.encode("hello");
        ByteBufferUtil.debugAll(byteBuffer3);

        //注意上边后两种方法会自动切换读模式
        byteBuffer1.flip();
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer1).toString());
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer3).toString());

    }
}
