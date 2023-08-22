package com.ayx.c4;

import com.ayx.util.ByteBufferUtil;
import io.netty.channel.ServerChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * nio 阻塞模式demo socket channel版本
 */
@Slf4j
public class Server {
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

    public static void main(String[] args) throws IOException {
//        main1(new String[]{});
        main3(null);
    }

    //IO多路复用版本 selector
    public static void main3(String[] args) throws IOException {



        Selector selector = Selector.open();//创建一个selector 管理多个channel;
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);//这里必须是NIO
        //注册selector到serverSocketChannel中
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, null);
        //设置selector关注的事件类型
        selectionKey.interestOps(selectionKey.OP_ACCEPT);
        log.debug("register:{}", selectionKey);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        while (true) {

            //selector中的事件必须被处理或者关闭（cancel方法），不能不管，否则他会一直往Set中添加
            selector.select();//这仍然是一个阻塞方法 当关注的时间发生后可以继续向下运行

            //处理事件
            Iterator<SelectionKey> its = selector.selectedKeys().iterator();
            while (its.hasNext()) {
                SelectionKey key = its.next();


                log.debug("key:{}", key);
                its.remove();//非常重要的代码，删除selectedKeys中被处理过的channel

                //需要对事件进行区分,不同的事件做不同的操作
                if (key.isAcceptable()) {//如果是accept事件
                    ServerSocketChannel ServerSocketChannel = (ServerSocketChannel) key.channel();
                    //拿到socket进行处理
                    SocketChannel socket = ServerSocketChannel.accept();
                    log.debug("sc:{}", socket);
                    socket.configureBlocking(false);
                    //给他也塞到selector中
                    //给Selector绑定一个附件（ByteBuffer）
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = socket.register(selector, 0, byteBuffer);
                    scKey.interestOps(SelectionKey.OP_READ);//关注read事件



                } else if (key.isReadable()) { //read事件
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                        int read = channel.read(byteBuffer);
                        if (read == -1) { //如果客户端是正常断开 read的返回值是-1
                            key.cancel();
                        }else {
                            split(byteBuffer);
                            if (byteBuffer.position() == byteBuffer.limit()){
                                ByteBuffer newbyteBuffer = ByteBuffer.allocate(byteBuffer.capacity()*2);
                                byteBuffer.flip();
                                newbyteBuffer.put(byteBuffer);
                                key.attach(newbyteBuffer);//将新的ByteBuffer重新注册到Selector上
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.channel();//因为客户端断开了，需要将Key取消（从Selector的Keys集合中真正删除Key）
                    }
                }

            }
        }

    }

    //非阻塞式
    public static void main1(String[] args) throws IOException {
        //使用NIO演示阻塞模式  单线程版本  像便秘一样，一次只能干一件事情

        // 使用 nio 来理解阻塞模式, 单线程
// 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
// 1. 创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

// 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false); //设置为非阻塞式
// 3. 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 4. accept 建立与客户端连接， SocketChannel 用来与客户端之间通信
//            log.debug("connecting...");
            SocketChannel sc = ssc.accept(); // 阻塞方法，线程停止运行
            if (sc != null) {
                log.debug("connected... {}", sc);
                sc.configureBlocking(false);//设置为非阻塞式
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                // 5. 接收客户端发送的数据
//                log.debug("before read... {}", channel);
                int read = channel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    ByteBufferUtil.debugAll(buffer);
                    buffer.clear();
                    log.debug("after read...{}", channel);
                }

            }
        }
    }

    public static void main2(String[] args) throws IOException {
        //使用NIO演示阻塞模式  单线程版本  像便秘一样，一次只能干一件事情

        // 使用 nio 来理解阻塞模式, 单线程
        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1. 创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 3. 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 4. accept 建立与客户端连接， SocketChannel 用来与客户端之间通信
            log.debug("connecting...");
            SocketChannel sc = ssc.accept(); // 阻塞方法，线程停止运行
            log.debug("connected... {}", sc);
            channels.add(sc);
            for (SocketChannel channel : channels) {
                // 5. 接收客户端发送的数据
                log.debug("before read... {}", channel);
                channel.read(buffer); // 阻塞方法，线程停止运行
                buffer.flip();
                ByteBufferUtil.debugAll(buffer);
                buffer.clear();
                log.debug("after read...{}", channel);
            }
        }

    }
}
