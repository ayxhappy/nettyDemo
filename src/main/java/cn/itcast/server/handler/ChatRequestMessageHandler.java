package cn.itcast.server.handler;

import cn.itcast.message.ChatRequestMessage;
import cn.itcast.message.ChatResponseMessage;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {

        Channel channel = SessionFactory.getSession().getChannel(msg.getTo());
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(true, msg.getContent()));
        } else {
            channel.writeAndFlush(new ChatResponseMessage(false, "用户已离线"));
        }
    }
}
