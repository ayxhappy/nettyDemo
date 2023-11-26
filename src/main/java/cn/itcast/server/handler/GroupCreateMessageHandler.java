package cn.itcast.server.handler;

import cn.itcast.message.GroupCreateRequestMessage;
import cn.itcast.message.GroupCreateResponseMessage;
import cn.itcast.server.session.Group;
import cn.itcast.server.session.GroupSessionFactory;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

@ChannelHandler.Sharable
public class GroupCreateMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().createGroup(msg.getGroupName(), msg.getMembers());
        if (group == null) {
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(true, "群聊:" + msg.getGroupName() + "创建成功"));

            //创建成功 给每个人发送拉群消息
            List<Channel> channelList = GroupSessionFactory.getGroupSession().getMembersChannel(msg.getGroupName());
            channelList.forEach(channel -> {
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已经被拉入" + msg.getGroupName() + "群聊"));
            });

        } else {
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(false, "群聊:" + msg.getGroupName() + "已经存在"));
        }
    }
}
