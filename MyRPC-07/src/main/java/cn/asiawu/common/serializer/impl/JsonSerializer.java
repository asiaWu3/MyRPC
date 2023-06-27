package cn.asiawu.common.serializer.impl;

import cn.asiawu.common.entity.RpcRequest;
import cn.asiawu.common.entity.RpcResponse;
import cn.asiawu.common.serializer.Serializer;
import cn.asiawu.constant.MessageType;
import cn.asiawu.constant.SerializerType;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;

import javax.management.DescriptorAccess;

/**
 * @author asiawu
 * @date 2023/06/25 20:20
 * @description: Json序列化实现
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes, int messageTypeCode) {
        MessageType messageType=MessageType.getEnumByTypeCode(messageTypeCode);
        switch (messageType) {
            case RPC_REQUEST:
                return JSON.parseObject(bytes,RpcRequest.class,JSONReader.Feature.SupportClassForName);
            case RPC_RESPONSE:
                return JSON.parseObject(bytes, RpcResponse.class,JSONReader.Feature.SupportClassForName);
            default:
                System.out.println("暂未实现");
        }
        return null;
    }

    @Override
    public int getTypeCode() {
        return SerializerType.JSON_SERIALIZER.getCode();
    }
}
