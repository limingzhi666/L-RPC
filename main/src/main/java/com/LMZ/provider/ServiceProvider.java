package com.LMZ.provider;

import com.LMZ.config.RpcServiceConfig;

/**
 * 存储和提供服务对象。
 *
 * 存放服务接口名与服务端对应的实现类
 * 服务启动时要暴漏相关的实现类
 * 根据request中的interface调用服务端中相关的实现类
 */
public interface ServiceProvider {

    /**
     * 添加服务
     *
     * @param rpcServiceConfig 服务相关属性
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * 得到服务
     *
     * @param rpcServiceName 服务名称
     */
    Object getService(String rpcServiceName);

    /**
     * 推送服务
     *
     * @param rpcServiceConfig 服务相关属性
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

}