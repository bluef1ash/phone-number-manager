package com.github.phonenumbermanager.util;

import com.github.phonenumbermanager.constant.SystemConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWTToken工具类
 *
 * @author 廿二月的天
 */
public class JwtTokenUtil {
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";

    /**
     * 通过Token获取用户名
     *
     * @param token Token字符串
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 通过Token获取创建时间
     *
     * @param token Token字符串
     * @return 创建时间
     */
    public static Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    /**
     * 通过Token获取过期时间
     *
     * @param token Token字符串
     * @return 过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * 通过用户对象生成Token
     *
     * @param userDetails 用户对象
     * @return Token字符串
     */
    public static String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 通过KEY生成Token
     *
     * @param claims 加密对象
     * @return Token字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate()).signWith(SignatureAlgorithm.HS512, SystemConstant.SECRET).compact();
    }

    /**
     * 验证Token是否刷新
     *
     * @param token Token字符串
     * @return 是否刷新
     */
    public static Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 刷新Token
     *
     * @param token 原Token
     * @return 刷新后的Token
     */
    public static String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证Token
     *
     * @param token       Token字符串
     * @param userDetails 用户对象
     * @return 是否有效
     */
    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 通过Token获取加密模块
     *
     * @param token Token字符串
     * @return 加密模块
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SystemConstant.SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 生成过期时间
     *
     * @return 过期时间
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + SystemConstant.EXPIRATION_TIME * 1000);
    }

    /**
     * 验证Token是否过期
     *
     * @param token 需要验证的Token
     * @return 是否过期
     */
    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 验证最后一次重置密码的时间
     *
     * @param created           创建时间
     * @param lastPasswordReset 最后一次重置密码的时间
     * @return 是否最后一次重置密码的时间
     */
    private static Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }
}
