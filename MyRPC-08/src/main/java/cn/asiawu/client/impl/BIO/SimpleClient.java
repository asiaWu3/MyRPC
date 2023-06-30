package cn.asiawu.client.impl.BIO;

import cn.asiawu.client.Client;
import cn.asiawu.common.entity.RpcRequest;
import cn.asiawu.common.entity.RpcResponse;
import cn.asiawu.common.serviceRegister.ServiceRegister;
import cn.asiawu.common.serviceRegister.impl.ZkServiceRegister;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import static cn.asiawu.constant.Constants.HOST;
import static cn.asiawu.constant.Constants.PORT;

/**
 * @author asiawu
 * @date 2023/06/24 21:20
 * @description:
 */
@Slf4j
public class SimpleClient implements Client {
    private static final ServiceRegister serviceRegister = new ZkServiceRegister();;


    public RpcResponse call(RpcRequest rpcRequest) {
        //寻找Server
        InetSocketAddress serverAddress = serviceRegister.lookupService(rpcRequest.getInterfaceName());
        //连接Server
        try (Socket socket = new Socket(serverAddress.getAddress(),serverAddress.getPort());
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            //发送RpcRequest
            outputStream.writeObject(rpcRequest);
            outputStream.flush();
            return (RpcResponse) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("client启动失败");
        }
        return null;
    }
}
