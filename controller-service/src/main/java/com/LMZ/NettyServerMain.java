package com.LMZ;

import com.LMZ.annotation.LRPCScan;
import com.LMZ.remoting.transport.netty.server.NettyRpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 服务器：通过@RpcService注解自动注册服务
 */
@LRPCScan(basePackage = {"com.LMZ"})
public class NettyServerMain {
    public static void main(String[] args) {
        //通过注解注册服务
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer  = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        //注册服务
        nettyRpcServer.start();
    }
}