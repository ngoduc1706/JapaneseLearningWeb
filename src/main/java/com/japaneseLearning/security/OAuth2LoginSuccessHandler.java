package com.japaneseLearning.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Success handler after Google OAuth2 login.
 * Redirects the user to the home page or dashboard.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getEmail();
        
        System.out.println("Google login success for: " + email);
        
        // Redirect to root or home page
        response.sendRedirect("/");
    }
}
