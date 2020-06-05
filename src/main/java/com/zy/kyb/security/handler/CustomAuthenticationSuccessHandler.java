package com.zy.kyb.security.handler;

import cn.hutool.http.ContentType;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import com.zy.kyb.enums.ConstantEnum;
import com.zy.kyb.security.WxAppletAuthenticationToken;
import com.zy.kyb.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 用户认证通过的处理handler
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        // 使用jwt管理，所以封装用户信息生成jwt响应给前端
        String token = jwtTokenUtils.generateToken(((WxAppletAuthenticationToken)authentication).getOpenid());
        Map<String, Object> result = Maps.newHashMap();
        result.put(ConstantEnum.AUTHORIZATION.getValue(), token);
        httpServletResponse.setContentType(ContentType.JSON.toString());
        httpServletResponse.getWriter().write(JSON.toJSONString(result));
    }
}
