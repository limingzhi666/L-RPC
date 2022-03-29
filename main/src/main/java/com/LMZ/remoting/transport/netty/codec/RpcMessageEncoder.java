package com.LMZ.remoting.transport.netty.codec;


import com.LMZ.compress.Compress;
import com.LMZ.enums.CompressTypeEnum;
import com.LMZ.enums.SerializationTypeEnum;
import com.LMZ.extension.ExtensionLoader;
import com.LMZ.remoting.constants.RpcConstants;
import com.LMZ.remoting.dto.RpcMessage;
import com.LMZ.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义协议编码器
 * <p>
 * 0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 * +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 * |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 * +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 * |                                                                                                       |
 * |                                         body                                                          |
 * |                                                                                                       |
 * |                                        ... ...                                                        |
 * +-------------------------------------------------------------------------------------------------------+
 * 4字节  magic code（魔法数）   1字节 version（版本）   4字节 full length（消息长度）    1字节 messageType（消息类型）
 * 1字节 compress（压缩类型） 1字节 codec（序列化类型）    4字节 requestId（请求的Id）
 * body（object类型数据）
 * <p>
 * LengthFieldBasedFrameDecoder 是一个基于长度的解码器，用于解决 TCP 解包和粘连问题。
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            // 留个地方写全长的值
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            //int占4个字节
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // 构建全长
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // 如果message Type不是心跳消息，full Length = head length + body length
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // 序列化对象
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("编解码器 name: [{}] ", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // 压缩字节
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }
            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            //获取writeIndex的值
            int writeIndex = out.writerIndex();
            //将之前空出的fullLength的位置填补
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("编码请求错误!", e);
        }
    }
}