package cn.asiawu.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author asiawu
 * @date 2023/06/24 22:08
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    /**
     * 成功/错误码
     */
    private int code;
    /**
     * 要返回的数据
     */
    private Object data;
    /**
     * 错误信息
     */
    private String message;

    public static RpcResponse success(Object data) {
        return new RpcResponse(200,data,null);
    }

    public static RpcResponse error(String message) {
        return new RpcResponse(500,null,"server出错啦");
    }
}
