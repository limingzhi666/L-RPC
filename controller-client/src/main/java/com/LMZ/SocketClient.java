package com.LMZ;

import com.LMZ.config.RpcServiceConfig;
import com.LMZ.proxy.RpcClientProxy;
import com.LMZ.remoting.transport.RpcRequestTransport;
import com.LMZ.remoting.transport.socket.SocketRpcClient;

public class SocketClient {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        //String hello = helloService.Hello(new Hello("111", "222"));
        //System.out.println(hello);
    }
}