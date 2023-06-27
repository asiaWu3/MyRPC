package cn.asiawu.common.codec;

import cn.asiawu.common.serializer.Serializer;
import cn.asiawu.constant.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.List;

import static cn.asiawu.constant.Constants.MAGIC_NUMBER;

/**
 * @author asiawu
 * @date 2023/06/26 00:11
 * @description: 按照自定义的消息格式解码数据
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        //获取魔法数
        int magicNumber = byteBuf.readInt();
        if (magicNumber!=MAGIC_NUMBER) {
            //魔法数错误，不解析
            return;
        }
        //获取消息类型
        int messageTypeCode = byteBuf.readInt();
        if (messageTypeCode!= MessageType.RPC_REQUEST.getCode()&&
        messageTypeCode!=MessageType.RPC_RESPONSE.getCode()) {
            log.error("暂不支持");
            return;
        }
        //读取序列化方式
        int serializerTypeCode = byteBuf.readInt();
        //获取序列化器
        Serializer serializer = Serializer.getSerializerByTypeCode(serializerTypeCode);
        //读取消息体长度
        int length = byteBuf.readInt();
        //解析消息体
        byte[] bytes=new byte[length];
        byteBuf.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, messageTypeCode);
        list.add(obj);
    }
}
