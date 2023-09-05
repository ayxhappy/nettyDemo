package com.ayx.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        //1.启动类
        ChannelFuture channelFuture = new Bootstrap()
                //2.添加EventLoop
                .group(new NioEventLoopGroup())
                //3.选择客户端Channel实现
                .channel(NioSocketChannel.class)
                //4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                //5.连接服务器
                .connect("localhost", 8080);
        //connect方法是一个异步非阻塞方法，真正执行connect的NIO的线程，所以必须调用sync（阻塞）方法，等待连接上 Channel才能建立连接发送数据
        //netty中提供了两种处理方法
        //第一种使用sync方法等待连接
      /*  channelFuture.sync();
        Channel channel = channelFuture.channel();
        channel.writeAndFlush("hello netty");
        System.out.println(channel);
        System.out.println("");*/
        //第二种 使用ChannelFuture回调方法
        channelFuture.addListener(new ChannelFutureListener() {
            //NIO建立好连接后会回调这个方法
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                channel.writeAndFlush("hello netty");
                System.out.println(channel);
                System.out.println("");
            }
        });

    }
}
