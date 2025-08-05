package com.teemlaren.chatApplication.controller;




import com.teemlaren.chatApplication.entity.AuthRequest;
import com.teemlaren.chatApplication.entity.AuthResponse;
import com.teemlaren.chatApplication.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtil;

    /**
     * Handles user authentication requests.
     * Authenticates the user with provided credentials and returns a JWT if successful.
     * @param authRequest The authentication request containing username and password.
     * @return ResponseEntity with AuthResponse containing JWT or an error message.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            // Attempt to authenticate the user using Spring Security's AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // If authentication fails due to bad credentials, throw a specific exception
            throw new BadCredentialsException("Incorrect username or password", e);
        } catch (Exception e) {
            // Catch any other authentication-related exceptions
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }

        // If authentication is successful, load UserDetails and generate JWT
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Return the JWT in the response
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}

