package cn.asiawu.constant;

import lombok.AllArgsConstructor;

/**
 * @author asiawu
 * @date 2023/06/25 22:58
 * @description: 消息类型枚举类
 */
public enum MessageType {
    RPC_REQUEST(1),
    RPC_RESPONSE(2),
    PING(3),
    PONG(4);
    private int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MessageType getEnumByTypeCode(int code) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getCode()==code) {
                return messageType;
            }
        }
        return null;
    }
}
