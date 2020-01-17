package com.xuecheng.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    @Test
    public void testRedis(){
        //定义key
        String key="xxxx";
        Map<String,String> value=new HashMap<>();
        value.put("jwt","xxxxx");
        value.put("refresh","yyy");

    }
}
