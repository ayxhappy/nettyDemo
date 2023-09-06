package com.ayx.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyFutureDemo {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        EventLoop eventLoop = group.next();

        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("计算结果中");
                TimeUnit.SECONDS.sleep(1);
                return 88;
            }
        });
        log.info("等待结果中");

        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.info("获得结果：{}",future.getNow());
            }
        });
    }
}
