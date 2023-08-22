package com.ayx.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("localhost", 8080));

        //读取大量数据，所以这里用死循环
        int count = 0;
        while (true) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
            count+= socketChannel.read(byteBuffer);
            System.out.println(count);
            byteBuffer.clear();
        }
    }
}
