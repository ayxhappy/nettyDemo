package cn.itcast.server;

import cn.itcast.message.LoginRequestMessage;
import cn.itcast.message.LoginResponseMessage;
import cn.itcast.protocol.MessageCodecSharable;
import cn.itcast.protocol.ProcotolFrameDecoder;
import cn.itcast.server.handler.*;
import cn.itcast.server.service.UserServiceFactory;
import com.sun.applet2.preloader.event.UserDeclinedEvent;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //业务处理器
        LoginHandler loginHandler = new LoginHandler();
        ChatRequestMessageHandler  chatRequestMessageHandler = new ChatRequestMessageHandler();
        GroupCreateMessageHandler groupCreateMessageHandler = new GroupCreateMessageHandler();
        GroupChatMessageHandler groupChatMessageHandler = new GroupChatMessageHandler();
        QuitHandler quitHandler = new QuitHandler();
        HealthCheckRequestHandler healthCheckRequestHandler = new HealthCheckRequestHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    //读事件5s/次 5s没有收到客户端的数据会触发一个IdleState#READ_IDLE事件。
                    ch.pipeline().addLast(new IdleStateHandler(5,0,0)); //添加心跳处理器
                    //添加一个双向处理器，监听IdleState#READ_IDLE 用来处理心跳的事件
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent stateEvent = (IdleStateEvent) evt;
                            if (stateEvent.state() == IdleState.READER_IDLE){
                                log.debug("读空闲事件已经超过5S了");
                                //异常失效的连接
                                ctx.channel().close();
                            }
                        }
                    });
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(loginHandler);
                    ch.pipeline().addLast(chatRequestMessageHandler);
                    ch.pipeline().addLast(groupCreateMessageHandler);
                    ch.pipeline().addLast(groupChatMessageHandler);
                    ch.pipeline().addLast(quitHandler);
                    ch.pipeline().addLast(healthCheckRequestHandler);


                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
