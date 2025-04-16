package com.example.socialmedia.controller;

import com.example.socialmedia.dto.UserDTO;
import com.example.socialmedia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.followUser(userDetails.getUsername(), userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unfollow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.unfollowUser(userDetails.getUsername(), userId);
        return ResponseEntity.ok().build();
    }
}