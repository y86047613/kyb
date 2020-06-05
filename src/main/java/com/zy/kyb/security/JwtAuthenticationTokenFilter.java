package com.zy.kyb.security;


import com.zy.kyb.entity.Account;
import com.zy.kyb.entity.Permission;
import com.zy.kyb.enums.ConstantEnum;
import com.zy.kyb.enums.ExceptionEnum;
import com.zy.kyb.excepiton.BasicException;
import com.zy.kyb.service.AuthService;
import com.zy.kyb.util.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用来校验请求头中的jwt是否有效，以此为依据来认证用户是否登录
 * @author tanwubo
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("processing authentication for [{}]", request.getRequestURI());
        String token = request.getHeader(ConstantEnum.AUTHORIZATION.getValue());
        String openid = null;
        if (token != null) {
            try {
                openid = jwtTokenUtils.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                log.error("an error occurred during getting username from token", e);
                throw new BasicException(ExceptionEnum.JWT_EXCEPTION.customMessage("an error occurred during getting username from token , token is [%s]", token));
            } catch (ExpiredJwtException e) {
                log.warn("the token is expired and not valid anymore", e);
                throw new BasicException(ExceptionEnum.JWT_EXCEPTION.customMessage("the token is expired and not valid anymore, token is [%s]", token));
            }catch (SignatureException e) {
                log.warn("JWT signature does not match locally computed signature", e);
                throw new BasicException(ExceptionEnum.JWT_EXCEPTION.customMessage("JWT signature does not match locally computed signature, token is [%s]", token));
            }
        }else {
            log.warn("couldn't find token string");
        }
        if (openid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("security context was null, so authorizing user");
            Account account = authService.findAccount(openid);
            List<Permission> permissions = authService.acquirePermission(account.getAccountId());
            List<SimpleGrantedAuthority> authorities = permissions.stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());
            log.info("authorized user [{}], setting security context", openid);
            SecurityContextHolder.getContext().setAuthentication(new WxAppletAuthenticationToken(openid, authorities));
        }
        filterChain.doFilter(request, response);
    }
}
