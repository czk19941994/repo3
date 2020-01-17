package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
   @Autowired
   private AuthService authService;
   @Value("${auth.cookieDomain}")
   private String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;
    @Override
    @RequestMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        if (loginRequest==null|| StringUtils.isEmpty(loginRequest.getUsername())){
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        if (loginRequest==null||StringUtils.isEmpty(loginRequest.getPassword())){
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        //申请令牌
        AuthToken authToken=authService.login(username,password,clientId,clientSecret);
        //短令牌
        String access_token = authToken.getAccess_token();//是否封装正确？？？
        this.saveCookie(access_token);
        //将令牌存储到cookie
        return new LoginResult(CommonCode.SUCCESS,access_token);
    }
    //将令牌存储到cookie
    private void saveCookie(String token){
        /**
         * 设置cookie
         *
         * @param response
         * @param name     cookie名字
         * @param value    cookie值
         * @param maxAge   cookie生命周期 以秒为单位
         */
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","uid",token,cookieMaxAge,false);
    }


    @Override
    public ResponseResult logout() {
        return null;
    }
}
