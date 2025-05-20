package com.dsol.salesflow.auth.controller;

import com.dsol.salesflow.auth.model.AuthenticationRequest;
import com.dsol.salesflow.auth.model.AuthenticationResponse;
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

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;

    @PostMapping(path = "/registration")
    public ResponseEntity<?> register(@RequestBody User user){
        authenticationService.registerUser(user);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            AuthenticationResponse response = authenticationService.authenticate(authRequest);
            return ResponseEntity.ok(response);
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping(path = "/validate/token/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token){
        Boolean validityResponse = authenticationService.validateToken(token);
        return ResponseEntity.ok(validityResponse);
    }
}
