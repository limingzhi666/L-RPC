package com.LMZ;

import com.LMZ.ServiceImpl.HelloServiceImpl;
import com.LMZ.config.RpcServiceConfig;
import com.LMZ.remoting.transport.socket.SocketRpcServer;

public class SocketServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(helloService);
        socketRpcServer.registerService(rpcServiceConfig);
        socketRpcServer.start();
    }
}