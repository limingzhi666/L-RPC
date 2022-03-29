package com.LMZ.spring;

import com.LMZ.annotation.LRPCScan;
import com.LMZ.annotation.LRPCRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;


/**
 * 扫描和过滤指定的注解
 */
@Slf4j
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final String SPRING_BEAN_BASE_PACKAGE = "com.LMZ";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        //RpcScan 注解中获取属性和值
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(LRPCScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        if (rpcScanAnnotationAttributes != null) {
            // 获取 basePackage 属性的值
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        // 扫描 RpcService 注解
        CustomScanner rpcServiceScanner = new CustomScanner(beanDefinitionRegistry, LRPCRegister.class);
        // 扫描Component 注解
        CustomScanner springBeanScanner = new CustomScanner(beanDefinitionRegistry, Component.class);
        log.info("                                 _                                  \n" +
                "                              _ooOoo_                               \n" +
                "                             o8888888o                              \n" +
                "                             88\" . \"88                              \n" +
                "                             (| -_- |)                              \n" +
                "                             O\\  =  /O                              \n" +
                "                          ____/`---'\\____                           \n" +
                "                        .'  \\\\|     |//  `.                         \n" +
                "                       /  \\\\|||  :  |||//  \\                        \n" +
                "                      /  _||||| -:- |||||_  \\                       \n" +
                "                      |   | \\\\\\  -  /'| |   |                       \n" +
                "                      | \\_|  `\\`---'//  |_/ |                       \n" +
                "                      \\  .-\\__ `-. -'__/-.  /                       \n" +
                "                    ___`. .'  /--.--\\  `. .'___                     \n" +
                "                 .\"\" '<  `.___\\_<|>_/___.' _> \\\"\".                  \n" +
                "                | | :  `- \\`. ;`. _/; .'/ /  .' ; |           \n" +
                "                \\  \\ `-.   \\_\\_`. _.'_/_/  -' _.' /                 \n" +
                "  ================-.`___`-.__\\ \\___  /__.-'_.'_.-'================  \n" +
                "                              `=--=-'                    lmz        ");
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
            int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
            log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);
            int rpcServiceCount = rpcServiceScanner.scan(rpcScanBasePackages);
            log.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceCount);
        }
    }
}