package com.zy.kyb.security;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSON;
import com.zy.kyb.entity.Account;
import com.zy.kyb.entity.Permission;
import com.zy.kyb.enums.ExceptionEnum;
import com.zy.kyb.excepiton.BasicException;
import com.zy.kyb.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 真正执行认证逻辑的manager, {@link WxAppletAuthenticationFilter}会将认证委托给{@link WxAppletAuthenticationManager}来做
 * @author tanwubo
 */
@Component
@Slf4j
public class WxAppletAuthenticationManager implements AuthenticationManager {
    @Autowired
    private AuthService authService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WxAppletAuthenticationToken wxAppletAuthenticationToken = null;
        if (authentication instanceof WxAppletAuthenticationToken) {
            wxAppletAuthenticationToken = (WxAppletAuthenticationToken) authentication;
        }
        Account account = authService.findAccount(wxAppletAuthenticationToken.getOpenid());
        //执行注册逻辑
        if (account == null) {
            log.debug("account not exist, began to register. openid is [{}]", wxAppletAuthenticationToken.getOpenid());
            //签名校验
            Digester digester = new Digester(DigestAlgorithm.SHA1);
            String data = wxAppletAuthenticationToken.getRawData() + wxAppletAuthenticationToken.getSessionKey();
            String signature = digester.digestHex(data);
            if (!wxAppletAuthenticationToken.getSignature().equals(signature)) {
                log.error("signature is invalid, [{}] vs [{}]", signature, wxAppletAuthenticationToken.getSignature());
                throw new BasicException(ExceptionEnum.SIGN_INVALID.customMessage("signature is invalid, [%s] vs [%s]", signature, wxAppletAuthenticationToken.getSignature()));
            }
            //获取用户信息
            account = JSON.parseObject(wxAppletAuthenticationToken.getRawData(), Account.class);
            account.setOpenid(wxAppletAuthenticationToken.getOpenid());
            account.setSessionKey(wxAppletAuthenticationToken.getSessionKey());
            account.setDeleted(0);
            log.info("account is [{}]", account);
            account = authService.register(account);
            //获取权限
            List<Permission> permissions = authService.acquirePermission(account.getAccountId());
            List<SimpleGrantedAuthority> authorities = permissions.stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());
            return new WxAppletAuthenticationToken(account.getOpenid(), account.getSessionKey(), authorities);
        }
        // 存在：获取权限
        List<Permission> permissions = authService.acquirePermission(account.getAccountId());
        List<SimpleGrantedAuthority> authorities = permissions.stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());
        return new WxAppletAuthenticationToken(wxAppletAuthenticationToken.getOpenid(), wxAppletAuthenticationToken.getSessionKey(), authorities);
    }

}
