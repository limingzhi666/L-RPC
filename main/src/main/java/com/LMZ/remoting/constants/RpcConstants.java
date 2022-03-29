package com.LMZ.remoting.constants;

/**
 * 定义常量
 */
public class RpcConstants {

    /**
     * 魔法数. 验证 RpcMessage
     *
     * 主要是为了筛选来到服务端的数据包，有了这个魔数之后，服务端首先取出前面四个字节进行比对，
     * 能够在第一时间识别出这个数据包并非是遵循自定义协议的，也就是无效数据包，为了安全考虑可以直接关闭连接以节省资源。
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    //public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    //版本信息
    public static final byte VERSION = 1;
    //消息总长
    public static final byte TOTAL_LENGTH = 16;
    //消息类型
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    /**
     * 心跳机制会设置消息类型为HEARTBEAT_REQUEST_TYPE，客户端接收到会发送消息类型为 HEARTBEAT_RESPONSE_TYPE的数据，来保证处于连接中
     */
    //ping 请求的心跳
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong 响应的心跳
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    public static final int HEAD_LENGTH = 16;

    //心跳机制发送的数据
    public static final String PING = "ping";
    public static final String PONG = "pong";

    //最大帧长度----它决定了可以接收的最大数据长度。如果超过，数据将被丢弃。
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}