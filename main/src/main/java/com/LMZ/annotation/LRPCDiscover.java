package com.LMZ.annotation;

import java.lang.annotation.*;

/**
 * RPC 引用注解，自动装配服务实现类 (服务发现)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited //如果一个类用上了@Inherited修饰的注解，那么其子类也会继承这个注解
public @interface LRPCDiscover {
    /**
     * 服务版本 LRPCDiscover
     */
    String version() default "";

    /**
     * 服务组
     */
    String group() default "";
}