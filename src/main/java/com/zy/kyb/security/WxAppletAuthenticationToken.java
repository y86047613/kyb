package com.zy.kyb.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 用于微信登录的{@link Authentication}
 * @author tanwubo
 */
@Getter
@Setter
public class WxAppletAuthenticationToken extends AbstractAuthenticationToken {
   private String openid;
   private String sessionKey;
   private String rawData;
   private String signature;

    public WxAppletAuthenticationToken(String openid, String sessionKey, String rawData, String signature) {
        super(null);
        this.openid = openid;
        this.sessionKey = sessionKey;
        this.rawData = rawData;
        this.signature = signature;
    }

    public WxAppletAuthenticationToken(String openid, String sessionKey, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.openid = openid;
        this.sessionKey = sessionKey;
        super.setAuthenticated(true);
    }

    public WxAppletAuthenticationToken(String openid, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.openid = openid;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return this.openid;
    }

    public Object getPrincipal() {
        return this.sessionKey;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }


}
