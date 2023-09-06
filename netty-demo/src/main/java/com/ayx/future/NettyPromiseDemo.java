package com.ayx.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyPromiseDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();

        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(()->{
            try {
                log.info("计算结果中");
                int i = 1 /0;
                TimeUnit.SECONDS.sleep(1);
                promise.setSuccess(88);//放入成功结果
            } catch (Exception e) {
                e.printStackTrace();
                promise.setFailure(e);//放入异常
            }
        }).start();
        log.info("等待结果中");
        log.info("获得结果：{}",promise.get());
    }
}
