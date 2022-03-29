package com.LMZ;


import com.LMZ.annotation.LRPCDiscover;
import org.springframework.stereotype.Component;

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