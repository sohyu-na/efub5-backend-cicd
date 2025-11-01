package efub.assignment.community.member.service;

import efub.assignment.community.global.utils.OAuth2UserInfo;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = new OAuth2UserInfo(oAuth2User.getAttributes());

        // DB 사용자 조회 *없으면 생성
        Member member = memberRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElseGet(()->createAccount(oAuth2UserInfo));

        // 사용자 속성 생성
        Map<String,Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("id", member.getMemberId());
        attributes.put("email", member.getEmail());

        // DefaultOAuth2User 객체 생성 후 반환
        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),attributes,"email");

    }

    // 사용자 생성 후 저장 및 반환
    private Member createAccount(OAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .password("")
                .nickname(oAuth2UserInfo.getNickname())
                .build();
        return memberRepository.save(member);
    }
}
