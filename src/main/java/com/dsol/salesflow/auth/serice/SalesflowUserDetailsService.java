package com.dsol.salesflow.auth.serice;

import com.dsol.salesflow.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class SalesflowUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
    }

    public Optional<UserDetails> findByEmail(String username) {
        User user = userService.findByEmail(username, true, false);
        if(user != null){
            UserDetails userDetails = getUserDetails(user);
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }

    public static UserDetails getUserDetails(User user) {
        UserDetails userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
            }
            @Override
            public String getPassword() {
                return user.getPassword();
            }
            @Override
            public String getUsername() {
                return user.getEmail();
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
