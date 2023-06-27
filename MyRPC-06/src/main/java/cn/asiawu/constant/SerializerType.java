package cn.asiawu.constant;

/**
 * @author asiawu
 * @date 2023/06/25 23:54
 * @description:
 */
public enum SerializerType {
    OBJECT_SERIALIZER(1),
    JSON_SERIALIZER(2),
    KRYO_SERIALIZER(3);

    private int code;

    SerializerType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SerializerType getEnumByTypeCode(int code) {
        for (SerializerType value : SerializerType.values()) {
            if (value.getCode()==code) {
                return value;
            }
        }
        return null;
    }
}
