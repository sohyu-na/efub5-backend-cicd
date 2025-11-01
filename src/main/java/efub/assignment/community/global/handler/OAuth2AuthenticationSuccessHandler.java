package efub.assignment.community.global.handler;

import efub.assignment.community.global.jwt.TokenProvider;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final String redirectUrl="http://localhost:8080/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 인증된 사용자 정보 가져오기
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        // email로 member 조회
        String email = oAuth2User.getAttribute("email");
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found. email="+email));

        String accessToken = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member);
        tokenProvider.saveRefreshToken(member.getMemberId(),refreshToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"access_token\":\"%s\",\"refresh_token\":\"%s\"}", accessToken, refreshToken));
        response.getWriter().flush();
    }
}
