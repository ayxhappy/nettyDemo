package com.ayx.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        //1.启动器 负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2. BossEventLoop ,WorkerEventLoop,事件处理组
                .group(new NioEventLoopGroup())
                //3.选择服务器的ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                //4.boos 负责处理连接worker(child)负责处理读写，决定了worker能够处理哪些操作(handler)
                .childHandler(
                        //5. channel代表和客户端进行数据读写的通道Initializer 初始化 ，他负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder()); //将ByteBuf转换为字符串
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){//自定义的handler
                            @Override//读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg); //打印数据
                            }
                        });
                    }
                })
                //绑定端口
                .bind(8080);
    }
}
