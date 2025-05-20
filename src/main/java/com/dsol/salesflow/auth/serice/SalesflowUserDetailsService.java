package com.dsol.salesflow.auth.serice;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class SalesflowUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(SalesflowUserDetailsService.class);
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
    }

    public Optional<UserDetails> findByEmail(String username) {
        logger.info("Fetch request from lms-auth-service: findByEmail , email={}", username);

        return Optional.empty();
    }

    public static UserDetails getUserDetails(User user) {
        UserDetails userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
//                return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
                return null;
            }
            @Override
            public String getPassword() {
//                return user.getPassword();
                return null;
            }
            @Override
            public String getUsername() {
//                return user.getEmail();
                return null;
            }
        };
        return userDetails;
    }


    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(this);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}
