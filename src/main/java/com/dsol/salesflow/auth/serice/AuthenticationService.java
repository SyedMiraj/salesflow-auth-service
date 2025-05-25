package com.dsol.salesflow.auth.serice;

import com.dsol.salesflow.auth.model.AuthenticationRequest;
import com.dsol.salesflow.auth.model.AuthenticationResponse;
import com.dsol.salesflow.auth.model.User;
import com.dsol.salesflow.auth.token.Token;
import com.dsol.salesflow.auth.token.TokenRepository;
import com.dsol.salesflow.auth.token.TokenType;
import com.dsol.salesflow.exception.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserService userService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userService.findByEmail(request.getEmail(), false, false);
        if(user != null){
            var jwtToken = jwtService.generateToken(user.getEmail());
            var refreshToken = jwtService.generateRefreshToken(user.getEmail());
            revokeAllUserTokens(user.getId());
            saveUserToken(user.getId(), jwtToken);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .role(user.getRole())
                    .build();
        } else{
            logger.error("Failed authentication from lms-gateway. Exception throws from lms-common-service. email={}", request.getEmail());
            throw new CommonException("14", "Authentication failed. Please contact administrator");
        }
    }

    private void revokeAllUserTokens(Long userId) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(userId);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(Long userId, String jwtToken) {
        var token = Token.builder()
                .userId(userId)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            throw new CommonException("11", "missing authorization header");
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            User userResponse = userService.findByEmail(userEmail, false, false);
            if(userResponse != null){
                if (jwtService.isTokenValid(refreshToken, userResponse.getEmail())) {
                    var accessToken = jwtService.generateToken(userResponse.getEmail());
                    revokeAllUserTokens(userResponse.getId());
                    saveUserToken(userResponse.getId(), accessToken);
                    var authResponse = AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                    return authResponse;
                }
            }
        } else {
            throw new CommonException("12", "un authorized access to application");
        }
        return null;
    }

    public Boolean validateToken(String token) {
        String email = jwtService.extractUsername(token);
        return jwtService.isTokenValid(token, email);
    }
}
