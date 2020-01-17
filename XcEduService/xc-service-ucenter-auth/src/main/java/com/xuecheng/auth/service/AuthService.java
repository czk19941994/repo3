package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${auth.tokenValiditySecond}")
    private long time;
    //
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //请求spring sercirity
        //获得地址
        ServiceInstance choose = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = choose.getUri();
        //拼接地址
        String authUrl=uri+"/auth/oauth/token";
        //body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grand_type","password");
        body.add("username",username);
        body.add("password",password);
        //header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = this.getHttpBasic(clientId, clientSecret);
        header.add("Authorization",httpBasic);
        HttpEntity<MultiValueMap<String,String>> httpEntity=new HttpEntity<>(body,header);
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        //得到内容
        Map body1 = exchange.getBody();
        if (body1==null||body1.get("access_token")==null||body1.get("refresh_token")==null||body1.get("jti")==null){
            return null;
        }
        AuthToken authToken=new AuthToken();
        authToken.setJwt_token((String) body1.get("jti"));
        authToken.setRefresh_token((String)body1.get("refresh_token"));
        authToken.setAccess_token("access_token");
        String jsonString = JSON.toJSONString(authToken);
        boolean save = save(authToken.getJwt_token(), jsonString, time);
        if (!save){
           return null;
        }
        return authToken;
    }
    //获取httpbasic串
    private String getHttpBasic(String clientId,String clientSecret){
        String string=clientId+":"+clientSecret;
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic  "+new String(encode);
    }
    //将token存入redis
    private boolean save(String access_token,String content,long time){
        String key="user_token"+access_token;
        stringRedisTemplate.boundValueOps(key).set(content,time, TimeUnit.SECONDS);
        //过期返回-2
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire>0;
    }
}
