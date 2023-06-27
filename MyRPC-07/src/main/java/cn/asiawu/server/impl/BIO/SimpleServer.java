package cn.asiawu.server.impl.BIO;

import cn.asiawu.common.entity.RpcRequest;
import cn.asiawu.common.entity.RpcResponse;
import cn.asiawu.common.serviceRegister.ServiceRegister;
import cn.asiawu.common.serviceRegister.impl.ZkServiceRegister;
import cn.asiawu.server.Server;
import cn.asiawu.server.ServiceProvider;
import cn.asiawu.service.AccountService;
import cn.asiawu.service.UserService;
import cn.asiawu.service.impl.AccountServiceImpl;
import cn.asiawu.service.impl.UserServiceImpl;
import jdk.internal.org.objectweb.asm.tree.ParameterNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;

import static cn.asiawu.constant.Constants.PORT;

/**
 * @author asiawu
 * @date 2023/06/25 01:41
 * @description: Server的简单实现，BIO同步阻塞式IO
 */
@Slf4j
public class SimpleServer implements Server {
    private ServiceProvider serviceProvider;
    private ServiceRegister serviceRegister;
    private String host;
    private int port;

    public SimpleServer() {
        this.port=PORT;
        init();
    }

    public SimpleServer(int port) {
        this.port = port;
        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        //初始化ServiceRegister
        serviceRegister=new ZkServiceRegister();
        //获取本机host
        try {
            host=InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        //初始化ServiceProvider,注册服务
        serviceProvider=new ServiceProvider();
        serviceProvider.addService(new UserServiceImpl());
        serviceRegister.register(UserService.class.getName(),new InetSocketAddress(host,port));
        serviceProvider.addService(new AccountServiceImpl());
        serviceRegister.register(AccountService.class.getName(),new InetSocketAddress(host,port));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //以BIO方式接收请求
            while (true) {
                Socket socket = serverSocket.accept();
                //获取RpcRequest
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
                //根据interfaceName拿到对应的Service对象
                Object serviceImpl = serviceProvider.getService(rpcRequest.getInterfaceName());
                //根据methodName找到目标方法
                Method method = serviceImpl.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                //执行目标方法
                Object res = method.invoke(serviceImpl, rpcRequest.getParams());
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

    @Override
    public void stop() {

    }
}
