package group8.EVBatterySwapStation_BackEnd.utils;

import group8.EVBatterySwapStation_BackEnd.entity.Driver;
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
    public Driver getCurrentUser(){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(object instanceof Driver){
            return (Driver) object;
        } else {
            // Handle the case where the principal is not an instance of User
            return null;
        }
    }

}
