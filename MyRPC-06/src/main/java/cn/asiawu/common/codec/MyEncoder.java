package cn.asiawu.common.codec;

import cn.asiawu.common.entity.RpcRequest;
import cn.asiawu.common.entity.RpcResponse;
import cn.asiawu.common.serializer.Serializer;
import cn.asiawu.constant.MessageType;
import cn.asiawu.constant.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

import static cn.asiawu.constant.Constants.MAGIC_NUMBER;

/**
 * @author asiawu
 * @date 2023/06/25 23:40
 * @description: 依次按照自定义的消息格式写入，传入的数据为request或者response
 * 需要持有一个serialize器，负责将传入的对象序列化成字节数组
 */
@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf byteBuf) throws Exception {
        //写入魔法数
        byteBuf.writeInt(MAGIC_NUMBER);
        //写入消息类型
        if (obj instanceof RpcRequest) {
            byteBuf.writeInt(MessageType.RPC_REQUEST.getCode());
        } else if(obj instanceof RpcResponse) {
            byteBuf.writeInt(MessageType.RPC_RESPONSE.getCode());
        } else {
            System.out.println("暂未实现");
        }
        //写入序列化方式
        byteBuf.writeInt(serializer.getTypeCode());
        //序列化消息体
        byte[] bytes = serializer.serialize(obj);
        //写入消息体长度
        byteBuf.writeInt(bytes.length);
        //写入序列化后的消息
        byteBuf.writeBytes(bytes);
    }
}
