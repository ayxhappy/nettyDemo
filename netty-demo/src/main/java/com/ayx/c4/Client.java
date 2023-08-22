package com.ayx.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {

        sendMessage();
        System.out.println("end....");
    }

    public static void demo() {
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 8080));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("waiting...");
    }

    public static void sendMessage(){
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 8080));
            sc.write(Charset.defaultCharset().encode("0123456780abcde333333\n"));
            sc.close(); //必须close负责 server端死循环
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
