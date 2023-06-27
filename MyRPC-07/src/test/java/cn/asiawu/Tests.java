package cn.asiawu;

import cn.asiawu.client.ClientProxy;
import cn.asiawu.client.impl.netty.NettyClient;
import cn.asiawu.common.entity.User;
import cn.asiawu.server.Server;
import cn.asiawu.server.ServiceProvider;
import cn.asiawu.server.impl.netty.NettyServer;
import cn.asiawu.service.AccountService;
import cn.asiawu.service.UserService;
import cn.asiawu.service.impl.AccountServiceImpl;
import cn.asiawu.service.impl.UserServiceImpl;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static cn.asiawu.constant.Constants.ZOOKEEPER_ADDRESS;

/**
 * @author asiawu
 * @date 2023/06/24 21:52
 * @description:
 */
public class Tests {
    private CuratorFramework zkClient;

    @Test
    public void testZookeeper() throws Exception {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.zkClient = CuratorFrameworkFactory.builder().connectString(ZOOKEEPER_ADDRESS)
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace("MyRPC").build();
        this.zkClient.start();

        //创建持久节点
//        zkClient.create().creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT).forPath("/UserService");
        //创建临时节点
        zkClient.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath("/UserService/127.0.0.1:8888");
        zkClient.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath("/UserService/127.0.0.1:8889");
        //获取所有UserService的地址
        List<String> list = zkClient.getChildren().forPath("/UserService");
        for (String s : list) {
            System.out.println(s);
        }
        //删除节点
        zkClient.delete().forPath("/UserService/127.0.0.1:8888");
        zkClient.delete().forPath("/UserService/127.0.0.1:8889");
    }

    @Test
    public void testServer() {
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.addService(new UserServiceImpl());
        serviceProvider.addService(new AccountServiceImpl());
        Server server = new NettyServer(serviceProvider);
        server.run();
    }

    @Test
    public void testClient() {
        UserService userServiceProxy = ClientProxy.getProxy(UserService.class, new NettyClient());
        User user = userServiceProxy.getUserById("123");
        System.out.println(user);
        AccountService accountServiceProxy = ClientProxy.getProxy(AccountService.class);
        double money = accountServiceProxy.getMoneyById("123");
        System.out.println(money);
    }
}
