package com.ayx.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class ByteBufferDemo {
    public static void main(String[] args) {
        //fileChannel
        //获取一个文件输入流
        try (FileChannel fileChannel = new FileInputStream("data.txt").getChannel()) {
            //创建一个缓冲区 10个字节
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while (true){
                //从缓冲区中读取数据
                int len = fileChannel.read(byteBuffer);
                log.debug("读取到的字节数 {}",len);
                if (len == -1){
                    break;
                }
                //打印
                //切换至读模式
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()){//是否还有剩余未读完的数据
                    byte b = byteBuffer.get();
                    log.debug("实际字节 {}",(char)b);
                }
                byteBuffer.clear(); //切换至写模式

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
