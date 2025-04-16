package com.example.socialmedia.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String profilePicture;
    private String bio;
    private int followersCount;
    private int followingCount;
}