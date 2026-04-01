package com.japaneseLearning.security;

import com.japaneseLearning.entity.ApplicationUser;
import com.japaneseLearning.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Custom implementation of UserDetailsService for Spring Security
 * Loads user information from the ApplicationUserRepository
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ApplicationUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by email or username
        ApplicationUser user = userRepository.findByEmail(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new User(
            user.getEmail(),
            user.getPasswordHash(),
            user.getEmailConfirmed() && !user.getLockoutEnabled(),
            true,
            true,
            true,
            getGrantedAuthorities(user)
        );
    }

    /**
     * Get authorities/roles for the user
     */
    private Collection<? extends GrantedAuthority> getGrantedAuthorities(ApplicationUser user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        System.out.println("[AUTH DEBUG] User: " + user.getEmail()
            + " | isAdmin field: " + user.getIsAdmin());
        if (Boolean.TRUE.equals(user.getIsAdmin())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            System.out.println("[AUTH DEBUG] ROLE_ADMIN granted to: " + user.getEmail());
        }
        return authorities;
    }
}
