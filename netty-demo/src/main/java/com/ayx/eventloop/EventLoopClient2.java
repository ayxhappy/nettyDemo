package com.ayx.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Scanner;

public class EventLoopClient2 {
    public static void main(String[] args) throws InterruptedException {
        //1.启动类
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                //2.添加EventLoop
                .group(group)
                //3.选择客户端Channel实现
                .channel(NioSocketChannel.class)
                //4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //添加一个日志输出处理器
                        nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                //5.连接服务器
                .connect("localhost", 8080);

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                channel.writeAndFlush("hello netty");
                System.out.println(channel);
                System.out.println("");
            }
        });

        Channel channel = channelFuture.channel();
        //新建一个线程来处理业务
        new Thread(()->{
            //循环录入键盘输入，发送,输入q退出
            Scanner scanner = new Scanner(System.in);
            while (true){
                String s = scanner.nextLine();
                if ("q".equals(s)){
                    channel.close();
//                    System.out.println("我是关闭连接后需要执行的代码");//错误使用，因为close也是一个异步方法
                    break;
                }
                channel.writeAndFlush(s);
            }
        },"input").start();

        //正确的执行关闭后的业务代码方法 和connect方法一样也是有两种处理方法
        ChannelFuture closeFuture = channel.closeFuture();
        /*closeFuture.sync();
        System.out.println("我是关闭连接后需要执行的代码");*/

        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("我是关闭连接后需要执行的代码");
                group.shutdownGracefully();//优雅关掉EventLoopGroup 程序就会停止了
            }
        });

    }
}
