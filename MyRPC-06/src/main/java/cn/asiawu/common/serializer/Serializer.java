package cn.asiawu.common.serializer;

import cn.asiawu.common.serializer.impl.JsonSerializer;
import cn.asiawu.common.serializer.impl.KryoSerializer;
import cn.asiawu.common.serializer.impl.ObjectSerializer;
import cn.asiawu.constant.SerializerType;

/**
 * @author asiawu
 * @date 2023/06/25 20:05
 * @description: 序列化器接口
 */
public interface Serializer {
    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return 序列化后的二进制数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes       要反序列化的byte数组
     * @param messageTypeCode 消息体的类型码
     * @return 反序列化后的对象
     */
    Object deserialize(byte[] bytes, int messageTypeCode);

    /**
     * 获取实现类的TypeCode
     * @return
     */

    int getTypeCode();

    /**
     * 根据typeNumber获取具体的序列化器类
     *
     * @param code
     * @return
     */
    static Serializer getSerializerByTypeCode(int code) {
        SerializerType serializerType = SerializerType.getEnumByTypeCode(code);
        switch (serializerType) {
            case OBJECT_SERIALIZER:
                return new ObjectSerializer();
            case JSON_SERIALIZER:
                return new JsonSerializer();
            case KRYO_SERIALIZER:
                return new KryoSerializer();
            default:
                return null;
        }
    }
}
