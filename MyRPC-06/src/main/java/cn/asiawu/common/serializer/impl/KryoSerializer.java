package cn.asiawu.common.serializer.impl;

import cn.asiawu.common.entity.RpcRequest;
import cn.asiawu.common.entity.RpcResponse;
import cn.asiawu.common.serializer.Serializer;
import cn.asiawu.constant.MessageType;
import cn.asiawu.constant.SerializerType;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author asiawu
 * @date 2023/06/25 20:26
 * @description: Kryo序列化实现类
 */
public class KryoSerializer implements Serializer {
    /**
     * Kryo非线程安全
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.register(Class[].class);
        kryo.register(Class.class);
        kryo.setReferences(true);//检测循环依赖，默认值为true,避免版本变化显式设置
        kryo.setRegistrationRequired(false);//默认值为true，避免版本变化显式设置
        ((DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());//设定默认的实例化器
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Output output = new Output(bos);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            output.close();
            kryoThreadLocal.remove();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageTypeCode) {
        Kryo kryo = kryoThreadLocal.get();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Input input = new Input(bis);
        Object obj=null;
        MessageType messageType=MessageType.getEnumByTypeCode(messageTypeCode);
        switch (messageType) {
            case RPC_REQUEST:
                obj=kryo.readObject(input, RpcRequest.class);
                break;
            case RPC_RESPONSE:
                obj=kryo.readObject(input, RpcResponse.class);
                break;
            default:
                System.out.println("暂未实现");
        }
        kryoThreadLocal.remove();
        input.close();
        return obj;
    }

    @Override
    public int getTypeCode() {
        return SerializerType.KRYO_SERIALIZER.getCode();
    }
}
