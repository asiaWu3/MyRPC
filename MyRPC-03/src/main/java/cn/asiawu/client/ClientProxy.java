package cn.asiawu.client;

import cn.asiawu.common.RpcRequest;
import cn.asiawu.common.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author asiawu
 * @date 2023/06/25 00:32
 * @description:
 */
public class ClientProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装RpcRequest对象
        RpcRequest rpcRequest=RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .build();
        //发起远程调用
        RpcResponse rpcResponse = new Client().call(rpcRequest);
        return rpcResponse.getData();
    }

    public static <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},new ClientProxy());
    }
}
