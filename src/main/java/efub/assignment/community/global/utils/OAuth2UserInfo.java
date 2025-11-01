package efub.assignment.community.global.utils;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class OAuth2UserInfo {
    private Map<String,Object> attributes;

    public String getEmail(){
        return (String) getKakaoAccount().get("email");
    }

    public String getNickname(){
        return (String) ((Map<String, Object>) getKakaoAccount().get("profile")).get("nickname");
    }

    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }
}
