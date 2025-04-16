package com.example.socialmedia.dto;

import com.example.socialmedia.model.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PostDTO {
    private Long id;
    private String content;
    private Set<String> mediaUrls;
    private Post.PrivacyLevel privacyLevel;
    private LocalDateTime createdAt;
    private UserDTO user;
    private int likesCount;
    private int commentsCount;
    private boolean likedByCurrentUser;
}