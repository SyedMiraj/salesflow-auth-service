package com.dsol.salesflow.auth.handler;

import com.dsol.salesflow.asset.Aggregate;
import com.dsol.salesflow.asset.CommandHandler;
import com.dsol.salesflow.auth.command.AuthenticationCommand;
import com.dsol.salesflow.auth.command.UserRegistrationCommand;
import com.dsol.salesflow.auth.model.AuthenticationRequest;
import com.dsol.salesflow.auth.model.AuthenticationResponse;
import com.dsol.salesflow.auth.model.User;
import com.dsol.salesflow.auth.serice.AuthenticationService;
import com.dsol.salesflow.auth.serice.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@Aggregate
@RequiredArgsConstructor
@Slf4j
public class AuthAggregate {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @CommandHandler
    @Transactional
    public void handleUserRegistrationCommand(UserRegistrationCommand command){
        User user = command.getPayload();
        userService.saveUser(user);
    }

    @CommandHandler
    @Transactional
    public AuthenticationResponse handleAuthenticationCommand(AuthenticationCommand command){
        AuthenticationRequest authRequest = command.getPayload();
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return authenticationService.authenticate(authRequest);
        }
        throw new RuntimeException("invalid access");
    }
}
