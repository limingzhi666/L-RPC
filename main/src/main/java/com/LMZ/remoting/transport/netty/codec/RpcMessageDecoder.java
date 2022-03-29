package com.LMZ.remoting.transport.netty.codec;


import com.LMZ.compress.Compress;
import com.LMZ.enums.CompressTypeEnum;
import com.LMZ.enums.SerializationTypeEnum;
import com.LMZ.extension.ExtensionLoader;
import com.LMZ.remoting.constants.RpcConstants;
import com.LMZ.remoting.dto.RpcMessage;
import com.LMZ.remoting.dto.RpcRequest;
import com.LMZ.remoting.dto.RpcResponse;
import com.LMZ.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 自定义协议解码器
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
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * lengthFieldOffset: 魔法数是4字节，版本是1字节，然后是全长。所以值为 5
     * lengthFieldLength: 消息长度是4字节。所以值为 4
     * lengthAdjustment: 消息长度包括所有数据并读取前9个字节，所以左边的长度是（fullLength-9）。所以值是-9
     * initialBytesToStrip: 我们将手动检查魔法数和版本，所以不要剥离任何字节。所以值为 0
     */
    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * maxFrameLength – 最大帧长度。它决定了可以接收的最大数据长度。如果超过，数据将被丢弃。
     * lengthFieldOffset – 长度字段偏移量。长度字段是跳过指定字节长度的字段。
     * lengthFieldLength – 长度字段中的字节数。
     * lengthAdjustment – 添加到长度字段值的补偿值
     * initialBytesToStrip – 跳过的字节数。如果你需要接收所有的header+body数据，如果你只想接收body数据，这个值为0，那么你需要跳过header消耗的字节数。
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("解码帧错误!", e);
                    throw e;
                } finally {
                    frame.release(); //释放ByteBuf
                }
            }
        }
        return decoded;
    }

    //解码帧---  todo 基于LengthFieldBasedFrameDecoder对传输协议二次封装？
    private Object decodeFrame(ByteBuf in) {
        // 注意：必须按顺序读取 ByteBuf
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        // 构建 RpcMessage 对象
        byte messageType = in.readByte();
        //编码类型
        byte codecType = in.readByte();
        //压缩类型
        byte compressType = in.readByte();
        //请求id
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // 解压字节
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(compressName);
            bs = compress.decompress(bs);
            // 反序列化对象
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("编解码器 name: [{}] ", codecName);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        // 读取版本并比较 一个字节
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("版本不兼容" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // 读取前 4 位，即魔法数，然后比较
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("未知的 magic code: " + Arrays.toString(tmp));
            }
        }
    }

}