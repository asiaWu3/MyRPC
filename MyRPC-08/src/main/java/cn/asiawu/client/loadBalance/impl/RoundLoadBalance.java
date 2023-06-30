package cn.asiawu.client.loadBalance.impl;

import cn.asiawu.client.loadBalance.LoadBalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author asiawu
 * @date 2023/06/30 20:17
 * @description: 轮询负载均衡
 */
public class RoundLoadBalance implements LoadBalance {
    private int index=-1;
    @Override
    public String balance(List<String> addresses) {
        int size = addresses.size();
        index=(index+1)%size;
        return addresses.get(index);
    }
}
