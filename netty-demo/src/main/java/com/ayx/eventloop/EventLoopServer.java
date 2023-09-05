package com.ayx.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        //再次细分
        EventLoopGroup eventLoopGroup = new DefaultEventLoop();
        new ServerBootstrap()
                //boss 和 worker
                //boss 负责NioEventLoopGroup 的accept事件 worker负责 NioEventLoopGroup 的read事件
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //读事件处理、
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.info(byteBuf.toString(Charset.defaultCharset()));
                                //将事件透传给下一个handler
                                ctx.fireChannelRead(msg);
                            }
                        }).addLast(eventLoopGroup,"handler2",new ChannelInboundHandlerAdapter(){
                            //使用group中的线程来处理耗时事件来达到异步的效果
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //读事件处理、
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.info(byteBuf.toString(Charset.defaultCharset()));
                            }
                        })
                        ;
                    }
                }).bind(8080);
    }
}
