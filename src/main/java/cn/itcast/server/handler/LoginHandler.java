package cn.itcast.server.handler;

import cn.itcast.message.LoginRequestMessage;
import cn.itcast.message.LoginResponseMessage;
import cn.itcast.server.service.UserServiceFactory;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


@ChannelHandler.Sharable
public class LoginHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        boolean flag = UserServiceFactory.getUserService().login(msg.getUsername(), msg.getPassword());
        LoginResponseMessage message;
        if (flag) {
            message = new LoginResponseMessage(true, "登录成功");
            //登录成功后保存会话
            SessionFactory.getSession().bind(ctx.channel(), msg.getUsername());
            ctx.writeAndFlush(message);
        } else {
            message = new LoginResponseMessage(false, "登录失败");
            ctx.writeAndFlush(message);
        }
    }
}
