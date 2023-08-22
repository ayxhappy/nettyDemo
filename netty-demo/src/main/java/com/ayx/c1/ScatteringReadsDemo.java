package com.ayx.c1;

import com.ayx.util.ByteBufferUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 集中读取demo
 */
public class ScatteringReadsDemo {
    public static void main(String[] args) {
      ;
        try (FileChannel channel = new RandomAccessFile("words.txt", "r").getChannel()) {
            ByteBuffer allocate1 = ByteBuffer.allocate(3);
            ByteBuffer allocate2 = ByteBuffer.allocate(3);
            ByteBuffer allocate3 = ByteBuffer.allocate(4);
            channel.read(new ByteBuffer[]{allocate1,allocate2,allocate3});
            allocate1.flip();
            allocate2.flip();
            allocate3.flip();
            ByteBufferUtil.debugAll(allocate1);
            ByteBufferUtil.debugAll(allocate2);
            ByteBufferUtil.debugAll(allocate3);
        } catch (IOException e) {
        }
    }
}
