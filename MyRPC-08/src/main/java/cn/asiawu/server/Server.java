package cn.asiawu.server;

import cn.asiawu.common.serviceRegister.ServiceRegister;
import cn.asiawu.common.serviceRegister.impl.ZkServiceRegister;

/**
 * @author asiawu
 * @date 2023/06/25 01:39
 * @description: RpcServer的抽象接口
 */
public interface Server {
    /**
     * 开启服务
     */
    void run();

    /**
     * 停止服务
     */
    void stop();
}
