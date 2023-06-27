package cn.asiawu;

import cn.asiawu.client.Client;
import cn.asiawu.common.RpcRequest;
import cn.asiawu.common.RpcResponse;
import cn.asiawu.common.User;
import cn.asiawu.server.Server;
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
        RpcResponse response1 = new Client().call(new RpcRequest("UserService","getUserById",new Object[]{"123"},new Class[]{String.class}));
        System.out.println(response1);
        RpcResponse response2 = new Client().call(new RpcRequest("UserService","getUserByUsername",new Object[]{"asiaWu3"},new Class[]{String.class}));
        System.out.println(response2);
    }
}
