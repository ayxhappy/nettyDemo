package com.ayx.c4;

import com.ayx.util.ByteBufferUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程版服务端
 * 多个线程绑定多个selector
 */
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(8080));

        serverSocketChannel.configureBlocking(false);

        Selector boss = Selector.open();

        SelectionKey bossKey = serverSocketChannel.register(boss, 0, SelectionKey.OP_ACCEPT);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        //创建固定数量的Worker
        Worker[] works = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < works.length; i++) {
            works[i] = new Worker("worker-"+i);
        }

      //计数器
        AtomicInteger index = new AtomicInteger();

        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {

                    SocketChannel socketChannel = serverSocketChannel.accept();

                    socketChannel.configureBlocking(false);
                    log.debug("connected...{}",socketChannel.getRemoteAddress());

                    //2.关联worker
                    log.debug("before register...{}", socketChannel.getRemoteAddress());

                    works[index.getAndIncrement() % works.length].register(socketChannel); //初始化selector,启动worker-0
                    log.debug("after register...{}", socketChannel.getRemoteAddress());

                }


            }
        }
    }
}

@Data
@Slf4j
class Worker implements Runnable {
    private Thread thread;
    private Selector selector;
    private String name;

    private ConcurrentLinkedDeque<Runnable> queue = new ConcurrentLinkedDeque<>();

    private volatile AtomicBoolean initFlag = new AtomicBoolean(false);

    public Worker(String name) {
        this.name = name;
    }

    //初始化方法 初始化线程和Selector
    public void register(SocketChannel socketChannel) throws IOException {
        if (!initFlag.get()) {
            selector = Selector.open();
            thread = new Thread(this, name);
            thread.start();
            initFlag.set(true);
        }
        selector.wakeup(); // 唤醒selector 让事件不必等待就可以注册上
        socketChannel.register(getSelector(),SelectionKey.OP_READ);
        /*//使用队列实现 两个线程之间的通信
        queue.add(()->{
            try {
                socketChannel.register(getSelector(),SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }
        });*/



    }

    @Override
    public void run() {

        //专门处理读事件
        while (true) {
            try {
                selector.select(); // worker-o
               /* Runnable task = queue.poll();
                if (task!=null){
                    task.run(); //执行socketChannel.register
                }*/
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        //这里省略粘包、半包、ByteBuffer扩容等代码
                        ByteBuffer byteBuffer =  ByteBuffer.allocate(16);
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        log.debug("read...{}", socketChannel.getRemoteAddress());
                        socketChannel.read(byteBuffer);
                        byteBuffer.flip();
                        ByteBufferUtil.debugAll(byteBuffer);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
