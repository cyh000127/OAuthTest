package test.oauthtest.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import test.oauthtest.member.entity.Role;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expiration}") long validityInMilliseconds,
                            @Value("${jwt.refresh-expiration}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = validityInMilliseconds;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    // AccessToken 생성
// Access Token 생성
    public String createAccessToken(Long kakaoId, Role role) {
        return createToken(kakaoId, role, accessTokenValidity);
    }

    //RefreshToken 생성
    public String createRefreshToken(Long kakaoId) {
        return createToken(kakaoId, Role.USER, refreshTokenValidity);
    }

    public String createToken(Long kakaoId, Role role, long validity) {
        Claims claims = Jwts.claims().subject(String.valueOf(kakaoId)).build();
        Date now = new Date();
        Date validityDate = new Date(now.getTime() + validity);

        var builder = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validityDate)
                .signWith(key);

        if (role != null) builder.claim("role", role.name());

        return builder.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return Jwts.parser().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}