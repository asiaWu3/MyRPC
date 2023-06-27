package cn.asiawu;

import cn.asiawu.client.Client;
import cn.asiawu.client.ClientProxy;
import cn.asiawu.common.RpcRequest;
import cn.asiawu.common.RpcResponse;
import cn.asiawu.common.User;
import cn.asiawu.server.Server;
import cn.asiawu.service.AccountService;
import cn.asiawu.service.UserService;
import org.junit.Test;

/**
 * @author asiawu
 * @date 2023/06/24 21:52
 * @description:
 */
public class Tests {
    @Test
    public void testServer() {
        new Server().run();
    }

    @Test
    public void testClient() {
        UserService userServiceProxy = ClientProxy.getProxy(UserService.class);
        User user = userServiceProxy.getUserById("123");
        System.out.println(user);
        AccountService accountServiceProxy=ClientProxy.getProxy(AccountService.class);
        double money = accountServiceProxy.getMoneyById("123");
        System.out.println(money);
    }
}
