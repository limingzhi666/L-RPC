package com.LMZ.ServiceImpl;


import com.LMZ.Hello;
import com.LMZ.HelloService;
import com.LMZ.annotation.LRPCRegister;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LRPCRegister(group = "test1", version = "version1")
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String Hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}