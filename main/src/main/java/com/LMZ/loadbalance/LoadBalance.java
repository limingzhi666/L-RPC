package com.LMZ.loadbalance;

import com.LMZ.extension.SPI;
import com.LMZ.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡策略接口
 */
@SPI
public interface LoadBalance {
    /**
     * 从现有服务地址列表中选择一个
     *
     * @param serviceAddresses 服务地址列表
     * @param rpcRequest
     * @return target 服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest);

}