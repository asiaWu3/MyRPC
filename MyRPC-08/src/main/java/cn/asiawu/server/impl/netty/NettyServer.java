package cn.asiawu.server.impl.netty;

import cn.asiawu.common.serviceRegister.ServiceRegister;
import cn.asiawu.common.serviceRegister.impl.ZkServiceRegister;
import cn.asiawu.server.Server;
import cn.asiawu.server.ServiceProvider;
import cn.asiawu.service.AccountService;
import cn.asiawu.service.UserService;
import cn.asiawu.service.impl.AccountServiceImpl;
import cn.asiawu.service.impl.UserServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;

import static cn.asiawu.constant.Constants.PORT;

/**
 * @author asiawu
 * @date 2023/06/25 13:46
 * @description: NettyServer
 */
public class NettyServer implements Server {
    private ServiceProvider serviceProvider;
    private ServiceRegister serviceRegister;
    private int port;
    private String host;

    public NettyServer(ServiceProvider serviceProvider) {
        this.serviceProvider=serviceProvider;
        this.port=PORT;
        init();
    }

    public NettyServer(int port,ServiceProvider serviceProvider) {
        this.serviceProvider=serviceProvider;
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
            host= InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        //注册服务
        Set<String> allServiceName = serviceProvider.getAllServiceName();
        for (String serviceName : allServiceName) {
            serviceRegister.register(serviceName,new InetSocketAddress(host,port));
        }

        // netty 服务线程组boss负责建立连接， work负责具体的请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 启动netty服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 初始化
            serverBootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serviceProvider));
            // 同步阻塞
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 死循环监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
