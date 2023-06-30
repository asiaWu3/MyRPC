package cn.asiawu.common.serializer.impl;

import cn.asiawu.common.serializer.Serializer;
import cn.asiawu.constant.SerializerType;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author asiawu
 * @date 2023/06/25 20:09
 * @description:
 */
@Slf4j
public class ObjectSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos=new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(bos)) {
            outputStream.writeObject(obj);
            outputStream.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageTypeCode) {
        try(ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
        ObjectInputStream inputStream=new ObjectInputStream(bis)) {
            Object obj = inputStream.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getTypeCode() {
        return SerializerType.OBJECT_SERIALIZER.getCode();
    }
}
