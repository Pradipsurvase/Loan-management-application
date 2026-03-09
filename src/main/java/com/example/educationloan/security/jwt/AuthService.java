package com.example.educationloan.security.jwt;




import com.example.educationloan.config.JwtTokenProvider;
import com.example.educationloan.dto.RegisterDTO;
import com.example.educationloan.entity.User;
import com.example.educationloan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.educationloan.dto.AuthDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthInterface {

    private final AuthenticationManager    authenticationManager;
    private final JwtTokenProvider         jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserService              userService;


    public AuthDTO login(String usernameOrEmail, String password) {

        // This triggers CustomUserDetailsService + BCrypt password check
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken  = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        log.info("User '{}' logged in successfully", userDetails.getUsername());
        return new AuthDTO(accessToken, refreshToken, userDetails.getUsername());
    }

    public AuthDTO register(RegisterDTO request) {
        User newUser = userService.registerUser(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getUsername());
        String accessToken  = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        log.info("User '{}' registered successfully", newUser.getUsername());
        return new AuthDTO(accessToken, refreshToken, newUser.getUsername());
    }

    public AuthDTO refresh(String refreshToken) {
        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String      username    = jwtTokenProvider.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String      newAccess   = jwtTokenProvider.generateAccessToken(userDetails);

        log.info("Access token refreshed for user '{}'", username);
        return new AuthDTO(newAccess, refreshToken, username);
    }
}