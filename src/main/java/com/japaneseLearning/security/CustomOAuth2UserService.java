package com.japaneseLearning.security;

import com.japaneseLearning.entity.ApplicationUser;
import com.japaneseLearning.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service to handle OAuth2 user information from Google Login.
 * Persists user information into ApplicationUser table if not present.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final ApplicationUserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        CustomOAuth2User customUser = new CustomOAuth2User(oauth2User);
        
        processOAuth2User(customUser);
        
        return customUser;
    }

    private void processOAuth2User(CustomOAuth2User oAuth2User) {
        String email = oAuth2User.getEmail();
        ApplicationUser existingUser = userRepository.findByEmail(email);

        if (existingUser == null) {
            // Register new user from Google
            ApplicationUser newUser = new ApplicationUser();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setEmail(email);
            newUser.setUsername(email); // Use email as username
            newUser.setNormalizedEmail(email.toUpperCase());
            newUser.setFirstName(oAuth2User.getFirstName());
            newUser.setLastName(oAuth2User.getLastName());
            newUser.setProvider("GOOGLE");
            newUser.setProviderId(oAuth2User.getAttribute("sub"));
            newUser.setEmailConfirmed(true); // Google email is verified
            
            userRepository.save(newUser);
            log.info("Registered new user from Google: {}", email);
        } else {
            // Update existing user with Google info if missing
            if (existingUser.getProvider() == null) {
                existingUser.setProvider("GOOGLE");
                existingUser.setProviderId(oAuth2User.getAttribute("sub"));
                userRepository.save(existingUser);
                log.info("Linked existing account with Google provider: {}", email);
            }
        }
    }
}
