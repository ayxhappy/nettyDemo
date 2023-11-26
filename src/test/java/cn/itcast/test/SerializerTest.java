package cn.itcast.test;

import cn.itcast.config.SysConfig;
import cn.itcast.message.LoginRequestMessage;
import cn.itcast.message.Message;
import cn.itcast.protocol.MessageCodecSharable;
import cn.itcast.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

public class SerializerTest {


    public static void main(String[] args) {

        MessageCodecSharable CODEC = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(loggingHandler, CODEC, loggingHandler);

        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "123", "");

        //embeddedChannel.writeOutbound(loginRequestMessage);



        embeddedChannel.writeInbound(messageToByteBuf(loginRequestMessage));


    }

    public static ByteBuf messageToByteBuf(Message message) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        // 1. 4 字节的魔数
        byteBuf.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本,
        byteBuf.writeByte(1);
        // 3. 1 字节的序列化方式 jdk 0 , json 1
        byteBuf.writeByte(0);
        // 4. 1 字节的指令类型
        byteBuf.writeByte(message.getMessageType());
        // 5. 4 个字节
        byteBuf.writeInt(message.getSequenceId());
        // 无意义，对齐填充
        byteBuf.writeByte(0xff);
        // 6. 获取内容的字节数组

        byte[] bytes = Serializer.SerializerImpl.values()[SysConfig.getSerializerType()].serializer(message);

        // 7. 长度
        byteBuf.writeInt(bytes.length);
        // 8. 写入内容
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

}
