package group8.EVBatterySwapStation_BackEnd.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private SecurityUtils() {
    }

    public static Long currentUserId() {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var jwt = auth.getToken();
        return Long.valueOf(jwt.getSubject());
    }
}
