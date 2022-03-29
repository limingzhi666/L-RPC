package com.LMZ.remoting.transport.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储和获取 Channel 对象
 */
@Slf4j
public class ChannelProvider {

    private final Map<String, Channel> channelMap;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    /**
     * 获取Channel
     */
    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        //判断对应地址是否有连接
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            //如果有连接并且可用，则直接获取
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                //说明连接不可用，从集合中移除
                channelMap.remove(key);
            }
        }
        return null;
    }

    /**
     * 添加Channel
     */
    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    /**
     * 删除Channel
     */
    public void delete(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
    }

}