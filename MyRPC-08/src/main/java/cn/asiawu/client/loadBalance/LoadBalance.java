package cn.asiawu.client.loadBalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author asiawu
 * @date 2023/06/30 20:10
 * @description: 负载均衡接口，传入
 */
public interface LoadBalance {
    /**
     * 负载均衡方法
     * @param addresses 传入所有服务列表
     * @return 返回其中一个
     */
    String balance(List<String> addresses);
}
