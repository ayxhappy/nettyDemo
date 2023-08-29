package com.ayx.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class MultiThreadClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("localhost", 8080));
        socketChannel.configureBlocking(false);
        socketChannel.write(Charset.defaultCharset().encode("0123456789"));

        System.in.read();
    }
}
