package cn.asiawu.client.impl.netty;

import cn.asiawu.common.codec.MyDecoder;
import cn.asiawu.common.codec.MyEncoder;
import cn.asiawu.common.serializer.Serializer;
import cn.asiawu.common.serializer.impl.KryoSerializer;
import cn.asiawu.server.ServiceProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.AllArgsConstructor;

/**
 * @author asiawu
 * @date 2023/06/25 13:33
 * @description: 初始化，主要负责序列化的编码解码， 需要解决netty的粘包问题
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    private Serializer serializer;
    public NettyClientInitializer(Serializer serializer) {
        this.serializer = serializer;
    }
    public NettyClientInitializer() {
        this.serializer=new KryoSerializer();
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        // 使用自定义的解码器
        pipeline.addLast(new MyDecoder());
        // 使用自定义的编码器
        pipeline.addLast(new MyEncoder(serializer));

        pipeline.addLast(new NettyClientHandler());
    }
}
