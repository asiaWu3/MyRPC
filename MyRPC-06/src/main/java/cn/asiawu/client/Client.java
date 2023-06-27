package cn.asiawu.client;

import cn.asiawu.common.entity.RpcRequest;
import cn.asiawu.common.entity.RpcResponse;

/**
 * @author asiawu
 * @date 2023/06/25 13:10
 * @description: Client抽象接口
 */
public interface Client {
    /**
     * 发起远程调用
     *
     * @param rpcRequest
     * @return
     */
    RpcResponse call(RpcRequest rpcRequest);
}
