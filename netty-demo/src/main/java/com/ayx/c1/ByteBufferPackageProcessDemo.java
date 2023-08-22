package com.ayx.c1;

import com.ayx.util.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * 粘包/半包处理demo
 * 粘包：Hello,world\nI'm zhangsan\nHo（前半部分）
 * 半包：w are you?\n（后半部分）
 */
public class ByteBufferPackageProcessDemo {

    public static void main(String[] args) {
        /**
         * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
         *
         * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
         *
         * - Hello,world\n
         * - I'm zhangsan\n
         * - How are you?\n
         *
         * 变成了下面的两个 byteBuffer (黏包，半包)
         *
         * - Hello,world\nI'm zhangsan\nHo
         * - w are you?\n
         *
         * 现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
         */
        ByteBuffer source = ByteBuffer.allocate(32);
        //                     11            24
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);

        source.put("w are you?\nhaha!\n".getBytes());
        split(source);
    }

    private static void split2(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                //将完整的消息存入新的byteBuffer
                int length = i + 1 - source.position();
                ByteBuffer byteBuffer = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    byteBuffer.put(source.get());
                }
                ByteBufferUtil.debugAll(byteBuffer);
            }
        }

        source.compact();
    }

    //第二种方法
    private static void split(ByteBuffer source) {
        source.flip();
        int oldLimit = source.limit();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                //将完整的消息存入新的byteBuffer
                ByteBuffer byteBuffer = ByteBuffer.allocate(i + 1 - source.position());
                // 0 ~ limit
                source.limit(i + 1);
                byteBuffer.put(source); // 从source 读，向 target 写
                source.limit(oldLimit);
                ByteBufferUtil.debugAll(byteBuffer);
            }
        }

        source.compact();
    }
}
