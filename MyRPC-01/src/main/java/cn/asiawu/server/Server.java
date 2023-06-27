package cn.asiawu.server;

import cn.asiawu.common.User;
import cn.asiawu.service.UserService;
import cn.asiawu.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static cn.asiawu.constant.Constants.PORT;

/**
 * @author asiawu
 * @date 2023/06/24 21:30
 * @description:
 */
@Slf4j
public class Server {
    public void run() {
        UserService userService=new UserServiceImpl();
        try(ServerSocket serverSocket=new ServerSocket(PORT)) {
            //以BIO方式接收请求
            while(true) {
                Socket socket = serverSocket.accept();
                //获取要查询的userId
                ObjectInputStream inputStream=new ObjectInputStream(socket.getInputStream());
                String userId = (String) inputStream.readObject();
                //查询
                User user = userService.getUserById(userId);
                //返回给client
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(user);
                outputStream.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("server启动失败");
        }
    }
}
