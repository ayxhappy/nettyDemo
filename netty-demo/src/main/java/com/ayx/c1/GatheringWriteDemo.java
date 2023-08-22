package com.ayx.c1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 集中写demo
 */
public class GatheringWriteDemo {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer byteBuffer1 = StandardCharsets.UTF_8.encode("word");
        ByteBuffer byteBuffer2 = StandardCharsets.UTF_8.encode("你好");

        try (FileChannel fileChannel = new RandomAccessFile("words2.txt", "rw").getChannel()) {
            fileChannel.write(new ByteBuffer[]{byteBuffer,byteBuffer1,byteBuffer2});
        } catch (IOException e) {
        }
    }
}
