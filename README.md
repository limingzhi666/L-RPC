# 使用

### 1.启动zookeeper作为服务注册中心

### 2.服务端

```java
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
```

### 3.controller提供接口

```java
@Component
public class HelloController {

    @LRPCDiscover(version = "version1", group = "test1")
    private HelloService helloService;

    public void test() {
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.Hello(new Hello("111", "222")));
        }
    }
}
```

### 4.客户端调用

```java
@LRPCScan(basePackage = {"com.LMZ"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
```

****************

************

# 包介绍
common-----工具类
controller---测试代码
main------主要代码

# 架构参考

![img](https://gitee.com/SnailClimb/guide-rpc-framework/raw/master/images/rpc-architure.png)

![image-20200805001037799](http://ganghuan.oss-cn-shenzhen.aliyuncs.com/img/image-20200805124759206.png)





# 学习中，有问题请指出

# 存在的问题

在不同端口启动多个服务端，关闭后注册过的信息不会消除，导致下次启动一个服务端时导致客户端数据发送与接收异常，存在BUG---待修复

客户端每次发起请求都要先与zookeeper进行通信得到地址，效率低下。

代码中很多地方标记了TODO，可以完善

.......等
