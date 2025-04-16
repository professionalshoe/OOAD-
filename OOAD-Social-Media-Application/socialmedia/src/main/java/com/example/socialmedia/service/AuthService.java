package com.example.socialmedia.service;

import com.example.socialmedia.dto.AuthResponse;
import com.example.socialmedia.dto.LoginRequest;
import com.example.socialmedia.dto.RegisterRequest;
import com.example.socialmedia.dto.UserDTO;
import com.example.socialmedia.exception.BadRequestException;
import com.example.socialmedia.model.User;
import com.example.socialmedia.repository.UserRepository;
import com.example.socialmedia.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                      AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(user);

        // Generate JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedUser.getUsername())
                .password(savedUser.getPassword())
                .authorities("ROLE_USER")
                .build();

        String token = jwtUtil.generateToken(userDetails);

        // Map user to DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(savedUser.getId());
        userDTO.setUsername(savedUser.getUsername());
        userDTO.setEmail(savedUser.getEmail());
        userDTO.setProfilePicture(savedUser.getProfilePicture());
        userDTO.setBio(savedUser.getBio());
        userDTO.setFollowersCount(0);
        userDTO.setFollowingCount(0);

        return new AuthResponse(token, userDTO);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            // Reset login attempts on successful login
            if (user.getLoginAttempts() > 0) {
                user.setLoginAttempts(0);
                user.setLocked(false);
                userRepository.save(user);
            }

            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Map user to DTO
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setProfilePicture(user.getProfilePicture());
            userDTO.setBio(user.getBio());
            userDTO.setFollowersCount(user.getFollowers().size());
            userDTO.setFollowingCount(user.getFollowing().size());

            return new AuthResponse(token, userDTO);
        } catch (BadCredentialsException e) {
            // Increment login attempts
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user != null) {
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                
                // Lock account after 5 failed attempts
                if (user.getLoginAttempts() >= 5) {
                    user.setLocked(true);
                }
                
                userRepository.save(user);
                
                if (user.isLocked()) {
                    throw new BadRequestException("Account locked due to too many failed login attempts");
                }
            }
            
            throw new BadRequestException("Invalid username or password");
        }
    }
}