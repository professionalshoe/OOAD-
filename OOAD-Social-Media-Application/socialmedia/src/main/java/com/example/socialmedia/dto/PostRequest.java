package com.example.socialmedia.dto;

import com.example.socialmedia.model.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Data
public class PostRequest {
    @NotBlank(message = "Content is required")
    private String content;
    private List<String> base64Images;

    private Set<String> mediaUrls = new HashSet<>();
    
    private Post.PrivacyLevel privacyLevel = Post.PrivacyLevel.PUBLIC;
}