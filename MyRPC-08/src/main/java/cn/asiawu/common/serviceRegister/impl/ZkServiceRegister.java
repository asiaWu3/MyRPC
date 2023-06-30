package cn.asiawu.common.serviceRegister.impl;

import cn.asiawu.client.loadBalance.LoadBalance;
import cn.asiawu.client.loadBalance.impl.RoundLoadBalance;
import cn.asiawu.common.serviceRegister.ServiceRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.asiawu.constant.Constants.PORT;
import static cn.asiawu.constant.Constants.ZOOKEEPER_ADDRESS;

/**
 * @author asiawu
 * @date 2023/06/26 22:58
 * @description: 基于Zookeeper的服务注册中心
 */
@Slf4j
public class ZkServiceRegister implements ServiceRegister {
    /**
     * zookeeper客户端
     */
    private CuratorFramework zkClient;
    /**
     * 根节点名
     */
    private static final String ROOT_NODE="MyRPC";

    /**
     * 负载均衡
     */
    private LoadBalance loadBalance=new RoundLoadBalance();
    /**
     * 存储所有服务
     */
    private static Map<String,List<String>> addressMap=new HashMap<>();
    /**
     * 在构造方法中完成zkClient的初始化
     */
    public ZkServiceRegister() {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.zkClient = CuratorFrameworkFactory.builder().connectString(ZOOKEEPER_ADDRESS)
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_NODE).build();
        this.zkClient.start();
    }

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            String servicePath="/" + serviceName;
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            if(zkClient.checkExists().forPath(servicePath) == null){
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            // 路径地址，一个/代表一个节点
            String path = "/" + serviceName + getServiceAddressStr(address);
            // 临时节点，服务器下线就删除节点
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ZNode:{} 不存在",serviceName);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<String> serviceAddresses = addressMap.get(serviceName);
        if (serviceAddresses==null) {
            serviceAddresses=getServerNodes(serviceName);
            addressMap.put(serviceName,serviceAddresses);
        }
        return getInetSocketAddress(loadBalance.balance(serviceAddresses));
    }

    /**
     * 拿到serviceName下的所有节点
     */
    private List<String> getServerNodes(String serviceName) {
        try {
            String servicePath="/" + serviceName;
            if(zkClient.checkExists().forPath(servicePath) == null){
                log.error("ZNode:{} 不存在",serviceName);
                return null;
            }
            // 服务名节点作为父节点
            String path = "/" + serviceName;
            //获取所有子节点
            List<String> services = zkClient.getChildren().forPath(path);
            //判断有无可用节点
            if (services==null||services.isEmpty()) {
                log.error("无可用服务:{}",serviceName);
                return null;
            }
            return services;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ZNode:{} 不存在",serviceName);
        }
        return null;
    }

    /**
     * 将InetSocketAddress类型的数据转换成字符串
     * @param address
     * @return
     */
    private String getServiceAddressStr(InetSocketAddress address) {
        //address存储结构 serviceName-->ip:port
        return address.getAddress()+":"+address.getPort();
    }

    /**
     * 将字符串地址解析成InetSocketAddress对象
     * @param address
     * @return
     */
    private InetSocketAddress getInetSocketAddress(String address) {
        String[] split = address.split(":");
        return new InetSocketAddress(split[0],Integer.parseInt(split[1]));
    }
}
