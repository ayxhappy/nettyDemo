package com.ayx.eventloop;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoopDemo {
    public static void main(String[] args) {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        System.out.println(NettyRuntime.availableProcessors());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        //一直采用轮训

        //执行普通任务 因为他也继承了Executor,所以他也是个线程池
        eventLoopGroup.next().submit(()->log.info("hello"));

        //执行
        eventLoopGroup.next().scheduleAtFixedRate(()->{log.info("hello2");},0,1, TimeUnit.SECONDS);
    }
}
