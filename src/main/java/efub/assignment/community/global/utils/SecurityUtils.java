package efub.assignment.community.global.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나,이름이 없는 경우
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return authentication.getName();
    }
}
