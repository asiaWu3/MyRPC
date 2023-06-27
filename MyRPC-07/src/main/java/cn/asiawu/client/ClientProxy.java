package cn.asiawu.client;

import cn.asiawu.client.impl.netty.NettyClient;
import cn.asiawu.common.entity.RpcRequest;
import cn.asiawu.common.entity.RpcResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author asiawu
 * @date 2023/06/25 00:32
 * @description:
 */
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    private Client client;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装RpcRequest对象
        RpcRequest rpcRequest=RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .build();
        //发起远程调用
        RpcResponse rpcResponse =client.call(rpcRequest);
        return rpcResponse.getData();
    }

    public static <T> T getProxy(Class<T> serviceInterface,Client client) {
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(),new Class[]{serviceInterface},new ClientProxy(client));
    }

    /**
     * 默认使用NettyClient
     */
    public static <T> T getProxy(Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(),new Class[]{serviceInterface},new ClientProxy(new NettyClient()));
    }
}
