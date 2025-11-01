package efub.assignment.community.member.service;

import efub.assignment.community.global.jwt.TokenProvider;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.dto.response.TokenResponseDto;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    // access token 재발급
    public TokenResponseDto reissueAccessToken(String refreshToken){
        String email = tokenProvider.extractEmail(refreshToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find member with email: " + email));

        String storedRefreshToken = redisTemplate.opsForValue().get(member.getMemberId().toString());
        if(!storedRefreshToken.equals(refreshToken)){
            throw new IllegalArgumentException("Refresh token does not match");
        }
        String accessToken = tokenProvider.createAccessToken(member);
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
