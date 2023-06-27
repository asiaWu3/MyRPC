package cn.asiawu;

import cn.asiawu.client.Client;
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
        for (int i = 0; i < 3; i++) {
            User user = (User) new Client().call("123");
            System.out.println(user);
        }
    }
}
