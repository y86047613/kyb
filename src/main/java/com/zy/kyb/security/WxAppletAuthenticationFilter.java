package com.zy.kyb.security;


import com.zy.kyb.dto.WxLoginResultDTO;
import com.zy.kyb.enums.ExceptionEnum;
import com.zy.kyb.excepiton.BasicException;
import com.zy.kyb.excepiton.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于用户认证的filter，但是真正的认证逻辑会委托给{@link WxAppletAuthenticationManager}
 * @author tanwubo
 */
@Slf4j
public class WxAppletAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.appsecret}")
    private String appsrcret;

    private String wxAuthUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    @Autowired
    private RestTemplate restTemplate;

    public WxAppletAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        String code = httpServletRequest.getParameter("code");
        if(StringUtils.isBlank(code)){
            log.error("code is null");
            throw new ParamException(ExceptionEnum.PARAM_EXCEPTION.customMessage("code is null"));
        }
        String rawData = httpServletRequest.getParameter("rawData");
        if(StringUtils.isBlank(rawData)){
            log.error("rawData is null");
            throw new ParamException(ExceptionEnum.PARAM_EXCEPTION.customMessage("rawData is null"));
        }
        String signature = httpServletRequest.getParameter("signature");
        if(StringUtils.isBlank(signature)){
            log.error("signature is null");
            throw new ParamException(ExceptionEnum.PARAM_EXCEPTION.customMessage("signature is null"));
        }
        String url = String.format(wxAuthUrl, appid, appsrcret, code);
        log.debug("wx auth url: [{}]", url);
        WxLoginResultDTO wxLoginResult = restTemplate.getForObject(url, WxLoginResultDTO.class);
        if(wxLoginResult.getErrcode() != null && !wxLoginResult.getErrcode().equals(0)){
            log.error("wx auth failed, errCode is [{}], errMsg is [{}]", wxLoginResult.getErrcode(), wxLoginResult.getErrmsg());
            throw new BasicException(ExceptionEnum.WX_AUTH_FAILED.customMessage("wx auth failed, errCode is [%s], errMsg is [%s]", wxLoginResult.getErrcode(), wxLoginResult.getErrmsg()));
        }
        log.info("wx login result: [{}]", wxLoginResult);
        return this.getAuthenticationManager().authenticate(new WxAppletAuthenticationToken(wxLoginResult.getOpenid(), wxLoginResult.getSession_key(), rawData, signature));
    }

}
