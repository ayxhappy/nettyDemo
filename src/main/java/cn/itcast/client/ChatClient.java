package cn.itcast.client;

import cn.itcast.message.*;
import cn.itcast.protocol.MessageCodecSharable;
import cn.itcast.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        AtomicBoolean LOGIN_FLAG = new AtomicBoolean(false);


        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    //添加心跳处理器，3s向服务发送一个数据包
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                    //添加一个双向处理器，监听IdleState#READ_IDLE 用来处理心跳的事件
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent stateEvent = (IdleStateEvent) evt;
                            if (stateEvent.state() == IdleState.WRITER_IDLE){
                                log.debug("向服务器发送心跳包");
                                //向服务器发送心跳包
                                ctx.channel().writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    //添加业务处理handler
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //新建一个线程用来接受用户的输入
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码");
                                String password = scanner.nextLine();
                                LoginRequestMessage message = new LoginRequestMessage();
                                message.setUsername(username);
                                message.setPassword(password);
                                //发送消息
                                ctx.writeAndFlush(message);

                                //等待输入
                                try {
                                    countDownLatch.await();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                //如果登录失败就退出
                                if (!LOGIN_FLAG.get()) {
                                    //退出
                                    ctx.channel().close();
                                    return;
                                }

                                while (true) {
                                    System.out.println("=============================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("=============================");
                                    System.out.println("请输入选项：");
                                    Scanner sc = new Scanner(System.in);
                                    String input = sc.nextLine();


                                    String[] split = input.split(" ");

                                    switch (split[0]) {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, split[1], split[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, split[1], split[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>(Arrays.asList(split[2].split(",")));
                                            set.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(split[1], set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(split[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, split[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, split[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;

                                    }
                                }
                            }, "user-input").start();
                        }

                        //接受响应消息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("msg:{}", msg);
                            //拿到登录结果
                            if (msg instanceof LoginResponseMessage) {
                                boolean loginFlag = ((LoginResponseMessage) msg).isSuccess();
                                LOGIN_FLAG.set(loginFlag);
                            }
                            countDownLatch.countDown();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
