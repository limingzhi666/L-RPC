package com.LMZ.loadbalance.loadbalancer;

import com.LMZ.loadbalance.AbstractLoadBalance;
import com.LMZ.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;


/**
 * 随机负载均衡策略的实现
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}