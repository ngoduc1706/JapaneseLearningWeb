package com.japaneseLearning.config;

import com.japaneseLearning.security.CustomOAuth2UserService;
import com.japaneseLearning.security.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oauth2UserService;
    private final OAuth2LoginSuccessHandler oauth2SuccessHandler;
    private final ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/index", "/about").permitAll()
                .requestMatchers("/login", "/register", "/forgot-password").permitAll()
                .requestMatchers("/home/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/courses/**", "/api/lessons/**").permitAll()
                .requestMatchers("/api/voucher/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/static/**", "/uploads/**").permitAll()
                // Authenticated user profiles (JL-26 to JL-30)
                .requestMatchers("/api/user/**").authenticated()
                // Admin-only routes
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/manage/**").hasRole("ADMIN")
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                // Allow both GET and POST /logout (no CSRF needed since csrf is disabled)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        // Enable OAuth2 login only when OAuth2 client registrations are configured.
        if (clientRegistrationRepositoryProvider.getIfAvailable() != null) {
            http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oauth2UserService)
                )
                .successHandler(oauth2SuccessHandler)
            );
        }
        
        return http.build();
    }
}
