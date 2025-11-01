package efub.assignment.community.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    private static final String BEARER = "Bearer ";
    private static final String HEADER = "Authorization";

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HEADER);
        String token = getAccessToken(authorizationHeader);

        if(!ObjectUtils.isEmpty(token) && tokenProvider.isValidToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    // authorization 헤더에서 bearer 제거 후 토큰 추출
    private String getAccessToken(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            return authorizationHeader.substring(BEARER.length());
        }
        return null;
    }
}
