package cn.asiawu.client.loadBalance.impl;

import cn.asiawu.client.loadBalance.LoadBalance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * @author asiawu
 * @date 2023/06/30 20:15
 * @description: 随机负载均衡
 */
public class RandomLoadBalance implements LoadBalance {
    private static Random random=new Random();
    @Override
    public String balance(List<String> addresses) {
        int size = addresses.size();
        return addresses.get(random.nextInt(size));
    }
}
