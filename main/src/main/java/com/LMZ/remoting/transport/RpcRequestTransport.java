package com.LMZ.remoting.transport;

import com.LMZ.extension.SPI;
import com.LMZ.remoting.dto.RpcRequest;

/**
 * 发送请求
 */
@SPI
public interface RpcRequestTransport {
    /**
     * 向服务器发送 rpc 请求并获取结果
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}