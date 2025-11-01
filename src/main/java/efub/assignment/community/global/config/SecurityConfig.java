package efub.assignment.community.global.config;

import efub.assignment.community.global.handler.OAuth2AuthenticationSuccessHandler;
import efub.assignment.community.global.jwt.JwtAuthenticationFilter;
import efub.assignment.community.global.jwt.TokenProvider;
import efub.assignment.community.member.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;

    // SecurityFilterChain 설정을 위한 Bean 등록
    // HTTP 요청에 대한 보안 구성을 정의하고 JWT 인증 필터 추가
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                // 기본 인증 방식 비활성화 <-토큰을 통한 인증
                .httpBasic(AbstractHttpConfigurer::disable)
                // CSRF 보호 비활성화 <-토큰 기반 인증
                .csrf(AbstractHttpConfigurer::disable)
                // cors 설정 추가
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 요청마다 인증 인가 설정
                .authorizeHttpRequests(request->{
                    request.requestMatchers("/**").permitAll();
                    request.anyRequest().authenticated();
                })
                // stateless <- 토큰 기반 인증
                .sessionManagement(
                        sessionManagement-> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // JWT 인증 필터를 앞에 추가
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                // OAuth2 로그인 설정
                .oauth2Login(oauth2->oauth2.userInfoEndpoint(userInfo-> userInfo.userService(customOAuth2UserService))
                        .successHandler(successHandler)).build();
    }
    // cors 설정 Bean 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 1. 허용할 출처(프론트엔드)를 명시
        configuration.addAllowedOrigin("http://localhost:3000");

        // 2. 허용할 HTTP 메서드(GET, POST 등)를 명시
        configuration.addAllowedHeader("*");

        // 3. 허용할 HTTP 헤더를 명시
        configuration.addAllowedMethod("*");

        // 4. 자격 증명(쿠키, 인증 헤더 등)을 허용할지 여부를 설정
        // true로 설정해야 Authorization 헤더에 담긴 JWT 토큰을 주고받기 가능
        configuration.setAllowCredentials(true);

        // 모든 경로(/)에 대해 위에서 정의한 CORS 설정을 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
