package com.ayx.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 零拷贝demo
 */
public class FileChannelTransferToDemo {


    //超过2G的文件就会变得很慢
    public static void main(String[] args) {
        try (FileChannel from = new FileInputStream("words.txt").getChannel();
             FileChannel to = new FileOutputStream("words3.txt").getChannel();
        ) {
            long size = from.size();
            for (long left = size; left < size; ) {
                System.out.println("position: " + (size - left) + "left:"+left);
                left -= from.transferTo(0, from.size(), to);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main2(String[] args) {
        try (FileChannel from = new FileInputStream("words.txt").getChannel();
             FileChannel to = new FileOutputStream("words3.txt").getChannel();
        ) {
            //零拷贝
            from.transferTo(0, from.size(), to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
