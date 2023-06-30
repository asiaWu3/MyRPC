package cn.asiawu.common.serviceRegister;

import java.net.InetSocketAddress;

/**
 * @author asiawu
 * @date 2023/06/26 22:52
 * @description: 注册中心 功能有：服务注册和服务查询
 */
public interface ServiceRegister {
    /**
     * 服务注册
     * @param serviceName 服务名
     * @param address 服务地址
     */
    void register(String serviceName, InetSocketAddress address);

    /**
     * 根据服务名查找一个服务
     * @param serviceName 服务名
     * @return
     */
    InetSocketAddress lookupService(String serviceName);
}
