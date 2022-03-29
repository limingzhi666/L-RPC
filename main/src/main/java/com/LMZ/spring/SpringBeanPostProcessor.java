package com.LMZ.spring;

import com.LMZ.annotation.LRPCDiscover;
import com.LMZ.annotation.LRPCRegister;
import com.LMZ.config.RpcServiceConfig;
import com.LMZ.extension.ExtensionLoader;
import com.LMZ.factory.SingletonFactory;
import com.LMZ.provider.ServiceProvider;
import com.LMZ.provider.impl.ZkServiceProviderImpl;
import com.LMZ.proxy.RpcClientProxy;
import com.LMZ.remoting.transport.RpcRequestTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 在创建bean之前调用这个方法来查看类是否被注解
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    //前置处理
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(LRPCRegister.class)) { //是否被注解
            log.info("[{}] 被注解为  [{}]", bean.getClass().getName(), LRPCRegister.class.getCanonicalName());
            // 获取 RpcService 注解
            LRPCRegister rpcService = bean.getClass().getAnnotation(LRPCRegister.class);
            //构建RpcServiceConfig
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean) //目标服务
                    .build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    //后置处理
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        //取出字段判断是否包含注解中的值
        for (Field declaredField : declaredFields) {
            LRPCDiscover rpcReference = declaredField.getAnnotation(LRPCDiscover.class);
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    //将指定对象参数上的此Field对象表示的字段设置为指定的新值。如果基础字段具有原始类型，则新值会自动展开
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}