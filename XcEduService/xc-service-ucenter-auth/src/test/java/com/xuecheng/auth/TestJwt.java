package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {
    //创建令牌
    @Test
    public void testCreateJwt(){
        //秘钥库文件
        String keystore="xc.keystore";
        //秘钥库密码
        String keystore_password="xuechengkeystore";
        //秘钥的密码
        String key_password="xuecheng";
        //秘钥别名
        String alias="xckey";
        //秘钥库路径
        ClassPathResource classPathResource=new ClassPathResource(keystore);
        //秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory=new KeyStoreKeyFactory(classPathResource,keystore_password.toCharArray());
        //得到秘钥对
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, key_password.toCharArray());
        //拿到私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //jwt令牌内容
        Map<String,String> body=new HashMap<>();
        body.put("name","itcast");
        String s = JSON.toJSONString(body);
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(s, new RsaSigner(aPrivate));
        //生成2jwt令牌编码
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
    //校验jwt令牌
    @Test
    public void testVerify(){
        //公钥
        String publicKey="";
        //jwt令牌
        String jwt="";
        Jwt jwt1 = JwtHelper.decodeAndVerify(jwt, new RsaVerifier(publicKey));
        //拿到其中的自定义内容
        String claims = jwt1.getClaims();
        System.out.println(claims);
    }
}
