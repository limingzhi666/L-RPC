package com.LMZ.annotation;

import java.lang.annotation.*;

/**
 * RPC服务注解，标注再服务实现类上 (服务注册)
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LRPCRegister {
    /**
     * 服务版本，默认值为空字符串
     */
    String version() default "";

    /**
     * 服务组，默认值为空字符串
     */
    String group() default "";
}