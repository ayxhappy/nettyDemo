package com.ayx.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {
    //服务端向客户端写入大量数据
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
//                    SelectionKey sckey = socketChannel.register(selector, SelectionKey.OP_READ);

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }

                    //实际写入的字节数
                    ByteBuffer byteBuffer = Charset.defaultCharset().encode(sb.toString());
                    while (byteBuffer.hasRemaining()) {
                        int write = socketChannel.write(byteBuffer);
                        System.out.println("写入字节数："+write);
                    }


                }
            }
        }
    }
}
