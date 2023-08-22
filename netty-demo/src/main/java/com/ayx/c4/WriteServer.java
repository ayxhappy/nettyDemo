package com.ayx.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
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

        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey sckey = socketChannel.register(selector, SelectionKey.OP_READ);

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    //实际写入的字节数
                    ByteBuffer byteBuffer = Charset.defaultCharset().encode(sb.toString());
                    //先写一次
                    int write = socketChannel.write(byteBuffer);
                    System.out.println("写入字节数：" + write);


                    //判断是否写完 没有写完关注写事件，交给处理写事件的业务代码来处理
                    if (byteBuffer.hasRemaining()) {

                        //关注可写事件
                        sckey.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                        //未写完的数据放到附件中下次处理
                        sckey.attach(byteBuffer);
                    }

                } else if (key.isWritable()) {
                    //处理写事件
                    ByteBuffer attachment = (ByteBuffer) key.attachment();
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    int write = socketChannel.write(attachment);
                    System.out.println("写入字节数：" + write);
                    //判断是否还有未处理完的数据
                    if (!attachment.hasRemaining()) {
                        //处理完了需要释放附件
                        key.attach(null);
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE); //无需关注写事件
                    }
                }
            }
        }
    }
}
