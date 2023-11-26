package cn.itcast.protocol;

import cn.itcast.message.Message;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 序列化接口
 */
public interface Serializer {

    //反序列化方法
    <T> T deSerializer(Class<T> clazz, byte[] bytes);

    //序列化方法
    <T> byte[] serializer(T object);


    ;//定义枚举实现

    enum SerializerImpl implements Serializer {

        Java {
            @Override
            public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public <T> byte[] serializer(T object) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = null;
                try {
                    oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return bos.toByteArray();
            }
        },
        Json {
            @Override
            public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
                String str = new String(bytes, StandardCharsets.UTF_8);
                return new Gson().fromJson(str, clazz);
            }

            @Override
            public <T> byte[] serializer(T object) {
                String json = new Gson().toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        },
        Hession {
            @Override
            public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
                return null;
            }

            @Override
            public <T> byte[] serializer(T object) {
                return null;
            }
        }
    }
}
