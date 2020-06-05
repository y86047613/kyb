package com.zy.kyb.util;

import cn.hutool.core.date.DateTime;
import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * jwt工具类
 * @author tanwubo
 */
@Component
public class JwtTokenUtils {

    @Value("${jwt.expiration}")
    private Long expiration;

    public SecretKey key;

    public JwtTokenUtils() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(this.key)
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String openid) {
        Map<String, Object> claims = Maps.newHashMap();
        return doGenerateToken(claims, openid);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = DateTime.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(this.key)
                .compact();
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

}
