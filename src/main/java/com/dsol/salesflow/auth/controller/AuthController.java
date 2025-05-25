package com.dsol.salesflow.auth.controller;

import com.dsol.salesflow.asset.CommandExecutor;
import com.dsol.salesflow.auth.command.AuthenticationCommand;
import com.dsol.salesflow.auth.command.UserRegistrationCommand;
import com.dsol.salesflow.auth.model.AuthenticationRequest;
import com.dsol.salesflow.auth.model.AuthenticationResponse;
import com.dsol.salesflow.auth.model.User;
import com.dsol.salesflow.auth.serice.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CommandExecutor commandExecutor;

    @PostMapping(path = "/registration")
    public ResponseEntity<?> register(@RequestBody User user){
        commandExecutor.execute(new UserRegistrationCommand(user));
        return ResponseEntity.accepted().build();
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authRequest) {
        AuthenticationResponse response = commandExecutor.execute(new AuthenticationCommand(authRequest));
        return ResponseEntity.accepted().body(response);
    }

//    @GetMapping(path = "/validate/token/{token}")
//    public ResponseEntity<Boolean> validateToken(@PathVariable String token){
//        Boolean validityResponse = authenticationService.validateToken(token);
//        return ResponseEntity.ok(validityResponse);
//    }
}
