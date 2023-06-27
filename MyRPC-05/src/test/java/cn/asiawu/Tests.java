package cn.asiawu;

import cn.asiawu.client.ClientProxy;
import cn.asiawu.client.impl.netty.NettyClient;
import cn.asiawu.common.User;
import cn.asiawu.server.Server;
import cn.asiawu.server.ServiceProvider;
import cn.asiawu.server.impl.netty.NettyServer;
import cn.asiawu.server.impl.threadPool.ThreadPoolServer;
import cn.asiawu.service.AccountService;
import cn.asiawu.service.UserService;
import cn.asiawu.service.impl.AccountServiceImpl;
import cn.asiawu.service.impl.UserServiceImpl;
import org.junit.Test;

/**
 * @author asiawu
 * @date 2023/06/24 21:52
 * @description:
 */
public class Tests {
    @Test
    public void testServer() {
        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.addService(new UserServiceImpl());
        serviceProvider.addService(new AccountServiceImpl());
        Server server=new NettyServer(serviceProvider);
        server.run();
    }

    @Test
    public void testClient() {
        UserService userServiceProxy = ClientProxy.getProxy(UserService.class,new NettyClient());
        User user = userServiceProxy.getUserById("123");
        System.out.println(user);
        AccountService accountServiceProxy=ClientProxy.getProxy(AccountService.class);
        double money = accountServiceProxy.getMoneyById("123");
        System.out.println(money);
    }
}
