package cn.asiawu.server;

import cn.asiawu.common.RpcRequest;
import cn.asiawu.common.RpcResponse;
import cn.asiawu.service.impl.UserServiceImpl;
import cn.asiawu.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        //创建UserService对象
        UserService userService = new UserServiceImpl();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            //以BIO方式接收请求
            while (true) {
                Socket socket = serverSocket.accept();
                //获取RpcRequest
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
                //根据methodName找到目标方法
                Method method = UserServiceImpl.class.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                //执行目标方法
                Object res = method.invoke(userService, rpcRequest.getParams());
                //封装RpcResponse
                RpcResponse rpcResponse = RpcResponse.success(res);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(rpcResponse);
                outputStream.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("server启动失败");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            log.error("server中没有目标方法");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            log.error("目标方法执行失败");
        } catch (IllegalAccessException e) {
            log.error("无权限执行目标方法");
        }
    }
}
