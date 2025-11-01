package efub.assignment.community.global.jwt;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    @Value("${jwt.secret.key}")
    private String secretKey;
    private static final Long accessTokenExpiration = 1000*60*60L;
    private static final Long refreshTokenExpiration = 1000*60*60*24*14L;
    private static final String AUTH_CLAIM = "auth";

    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // token 생성 및 저장
    public String createAccessToken(Member member){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpiration))
                .setSubject(member.getEmail())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String createRefreshToken(Member member){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiration))
                .setSubject(member.getEmail())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public void saveRefreshToken(Long userId, String refreshToken){
        redisTemplate.opsForValue().set(userId.toString(), refreshToken, Duration.ofMillis(refreshTokenExpiration));
    }

    // token 검사
    public boolean isValidToken(String token){
        try{
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            log.info("Validate token success");
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    // 토큰에서 권한 정보를 추출해 authentication 객체 생성
    public Authentication getAuthentication(String token){
        Claims claims = getClaims(token);

        // token에서 인가 추출
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails
                .User(claims.getSubject(), "", authorities), token, authorities);
    }
    private Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    // token에서 email 추출
    public String extractEmail(String token){
        if(isValidToken(token)){
            return getClaims(token).getSubject();
        }
        return null;
    }
}
