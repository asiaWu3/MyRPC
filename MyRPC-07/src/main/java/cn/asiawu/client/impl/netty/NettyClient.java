package cn.asiawu.client.impl.netty;

import cn.asiawu.client.Client;
import cn.asiawu.common.entity.RpcRequest;
import cn.asiawu.common.entity.RpcResponse;
import cn.asiawu.common.serializer.Serializer;
import cn.asiawu.common.serviceRegister.ServiceRegister;
import cn.asiawu.common.serviceRegister.impl.ZkServiceRegister;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

import static cn.asiawu.constant.Constants.HOST;
import static cn.asiawu.constant.Constants.PORT;

/**
 * @author asiawu
 * @date 2023/06/25 13:12
 * @description:
 */
public class NettyClient implements Client {
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private Serializer serializer;
    private static final ServiceRegister serviceRegister;

    private void setSerializer(Serializer serializer) {
        this.serializer=serializer;
    }

    // netty客户端初始化，重复使用
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());

        serviceRegister=new ZkServiceRegister();
    }

    /**
     * 这里需要操作一下，因为netty的传输都是异步的，你发送request，会立刻返回， 而不是想要的相应的response
     */
    @Override
    public RpcResponse call(RpcRequest request) {
        //寻找Server
        InetSocketAddress serverAddress = serviceRegister.lookupService(request.getInterfaceName());
        try {
            ChannelFuture channelFuture  = bootstrap.connect(serverAddress).sync();
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 实际上不应通过阻塞，可通过回调函数
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");
            RpcResponse response = channel.attr(key).get();
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
