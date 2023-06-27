package cn.asiawu.client;

import cn.asiawu.common.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static cn.asiawu.constant.Constants.HOST;
import static cn.asiawu.constant.Constants.PORT;

/**
 * @author asiawu
 * @date 2023/06/24 21:20
 * @description:
 */
@Slf4j
public class Client {
    public Object call(String id) {
        //连接Server
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            //发送要查询的用户的id
            outputStream.writeObject(id);
            outputStream.flush();
            User user = (User) inputStream.readObject();
            log.info("查询到User:{}", user);
            return user;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("client启动失败");
        }
        return null;
    }
}
